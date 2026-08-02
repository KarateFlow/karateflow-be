package com.karateflow.backend.athlete;

import com.karateflow.backend.BaseIntegrationTest;
import com.karateflow.backend.athlete.persistence.document.AthleteDocument;
import com.karateflow.backend.athlete.persistence.repository.AthleteMongoRepository;
import com.karateflow.backend.report.persistence.document.ReportDocument;
import com.karateflow.backend.report.persistence.repository.ReportMongoRepository;
import com.karateflow.backend.test.persistence.document.TestExecutionDocument;
import com.karateflow.backend.test.persistence.repository.TestExecutionMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AthleteIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AthleteMongoRepository repository;

    @Autowired
    private TestExecutionMongoRepository testExecutionRepository;

    @Autowired
    private ReportMongoRepository reportRepository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        testExecutionRepository.deleteAll();
        reportRepository.deleteAll();
    }

    @Test
    void shouldRetrieveAllAthletes() throws Exception {
        // Given
        final AthleteDocument a1 = AthleteDocument.builder()
                .firstName("Mario")
                .lastName("Rossi")
                .birthDate(LocalDate.of(2010, 5, 15))
                .build();
        final AthleteDocument a2 = AthleteDocument.builder()
                .firstName("Luigi")
                .lastName("Verdi")
                .birthDate(LocalDate.of(2011, 6, 20))
                .build();
        repository.saveAll(List.of(a1, a2));

        // When & Then
        mockMvc.perform(get("/api/v1/athletes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].firstName").value(org.hamcrest.Matchers.containsInAnyOrder("Mario", "Luigi")));
    }

    @Test
    void shouldCascadeDeleteAthlete() throws Exception {
        // Given
        final AthleteDocument athlete = AthleteDocument.builder()
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();
        final AthleteDocument savedAthlete = repository.save(athlete);
        final String athleteId = savedAthlete.getAthleteId();

        final TestExecutionDocument testExecution = TestExecutionDocument.builder()
                .athleteId(athleteId)
                .executionDate(LocalDateTime.now())
                .build();
        testExecutionRepository.save(testExecution);

        final ReportDocument report = ReportDocument.builder()
                .athleteId(athleteId)
                .createdAt(LocalDateTime.now())
                .build();
        reportRepository.save(report);

        // Verify inserted correctly
        assertThat(repository.findById(athleteId)).isPresent();
        assertThat(testExecutionRepository.findByAthleteIdOrderByExecutionDateDesc(athleteId)).hasSize(1);
        assertThat(reportRepository.findByAthleteIdOrderByCreatedAtDesc(athleteId)).hasSize(1);

        // When
        mockMvc.perform(delete("/api/v1/athletes/{athleteId}", athleteId))
                .andExpect(status().isNoContent());

        // Then
        assertThat(repository.findById(athleteId)).isEmpty();
        assertThat(testExecutionRepository.findByAthleteIdOrderByExecutionDateDesc(athleteId)).isEmpty();
        assertThat(reportRepository.findByAthleteIdOrderByCreatedAtDesc(athleteId)).isEmpty();
    }
}
