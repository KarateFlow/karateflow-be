package com.karateflow.backend.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.karateflow.backend.BaseIntegrationTest;
import com.karateflow.backend.athlete.persistence.document.AthleteDocument;
import com.karateflow.backend.athlete.persistence.repository.AthleteMongoRepository;
import com.karateflow.backend.report.dto.request.ReportPreviewRequestDTO;
import com.karateflow.backend.test.domain.model.MeasurementUnit;
import com.karateflow.backend.test.persistence.document.PerformedExerciseDocument;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReportIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AthleteMongoRepository athleteRepository;

    @Autowired
    private TestExecutionMongoRepository testRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        testRepository.deleteAll();
        athleteRepository.deleteAll();
    }

    @Test
    void shouldGenerateComparisonPreviewSuccessfully() throws Exception {
        // Given
        final AthleteDocument athlete = AthleteDocument.builder()
                .firstName("Mario")
                .lastName("Rossi")
                .birthDate(LocalDate.of(2010, 5, 15))
                .build();
        final AthleteDocument savedAthlete = athleteRepository.save(athlete);
        final String athleteId = savedAthlete.getAthleteId();

        final TestExecutionDocument testA = TestExecutionDocument.builder()
                .athleteId(athleteId)
                .executionDate(LocalDateTime.now().minusDays(10))
                .exercises(List.of(
                        PerformedExerciseDocument.builder()
                                .exerciseTitle("Pushups")
                                .result(20.0)
                                .unit(MeasurementUnit.COUNT)
                                .greaterIsBetter(true)
                                .build()
                ))
                .build();
        final TestExecutionDocument savedTestA = testRepository.save(testA);

        final TestExecutionDocument testB = TestExecutionDocument.builder()
                .athleteId(athleteId)
                .executionDate(LocalDateTime.now())
                .exercises(List.of(
                        PerformedExerciseDocument.builder()
                                .exerciseTitle("Pushups")
                                .result(25.0)
                                .unit(MeasurementUnit.COUNT)
                                .greaterIsBetter(true)
                                .build()
                ))
                .build();
        final TestExecutionDocument savedTestB = testRepository.save(testB);

        final ReportPreviewRequestDTO request = ReportPreviewRequestDTO.builder()
                .analysisType("COMPARISON")
                .athleteId(athleteId)
                .testIdA(savedTestA.getId())
                .testIdB(savedTestB.getId())
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/reports/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.athleteId").value(athleteId))
                .andExpect(jsonPath("$.analysisType").value("COMPARISON"))
                .andExpect(jsonPath("$.testIdA").value(savedTestA.getId()))
                .andExpect(jsonPath("$.testIdB").value(savedTestB.getId()))
                .andExpect(jsonPath("$.lowOverlap").value(false))
                .andExpect(jsonPath("$.comparisonResults[0].exerciseTitle").value("Pushups"))
                .andExpect(jsonPath("$.comparisonResults[0].resultA").value(20.0))
                .andExpect(jsonPath("$.comparisonResults[0].resultB").value(25.0))
                .andExpect(jsonPath("$.comparisonResults[0].delta").value("5.00"))
                .andExpect(jsonPath("$.comparisonResults[0].percentageChange").value("25.00"));
    }

    @Test
    void shouldGenerateTrendPreviewSuccessfully() throws Exception {
        // Given
        final AthleteDocument athlete = AthleteDocument.builder()
                .firstName("Mario")
                .lastName("Rossi")
                .birthDate(LocalDate.of(2010, 5, 15))
                .build();
        final AthleteDocument savedAthlete = athleteRepository.save(athlete);
        final String athleteId = savedAthlete.getAthleteId();

        final LocalDateTime now = LocalDateTime.now();

        final TestExecutionDocument test1 = TestExecutionDocument.builder()
                .athleteId(athleteId)
                .executionDate(now.minusDays(5))
                .exercises(List.of(
                        PerformedExerciseDocument.builder()
                                .exerciseTitle("Pushups")
                                .result(10.0)
                                .unit(MeasurementUnit.COUNT)
                                .greaterIsBetter(true)
                                .build()
                ))
                .build();
        testRepository.save(test1);

        final TestExecutionDocument test2 = TestExecutionDocument.builder()
                .athleteId(athleteId)
                .executionDate(now.minusDays(2))
                .exercises(List.of(
                        PerformedExerciseDocument.builder()
                                .exerciseTitle("Pushups")
                                .result(20.0)
                                .unit(MeasurementUnit.COUNT)
                                .greaterIsBetter(true)
                                .build()
                ))
                .build();
        testRepository.save(test2);

        final ReportPreviewRequestDTO request = ReportPreviewRequestDTO.builder()
                .analysisType("TREND")
                .athleteId(athleteId)
                .startDate(now.minusDays(10))
                .endDate(now)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/reports/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.athleteId").value(athleteId))
                .andExpect(jsonPath("$.analysisType").value("TREND"))
                .andExpect(jsonPath("$.exerciseTrends[0].exerciseTitle").value("Pushups"))
                .andExpect(jsonPath("$.exerciseTrends[0].dataPoints.length()").value(2))
                .andExpect(jsonPath("$.exerciseTrends[0].dataPoints[0].result").value(10.0))
                .andExpect(jsonPath("$.exerciseTrends[0].dataPoints[1].result").value(20.0));
    }
}
