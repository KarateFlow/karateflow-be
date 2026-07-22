package com.karateflow.backend.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.karateflow.backend.BaseIntegrationTest;
import com.karateflow.backend.athlete.persistence.document.AthleteDocument;
import com.karateflow.backend.athlete.persistence.repository.AthleteMongoRepository;
import com.karateflow.backend.report.dto.request.ReportPreviewRequestDTO;
import com.karateflow.backend.report.dto.request.ReportSaveRequestDTO;
import com.karateflow.backend.report.persistence.document.ReportDocument;
import com.karateflow.backend.report.persistence.repository.ReportMongoRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class ReportIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AthleteMongoRepository athleteRepository;

    @Autowired
    private TestExecutionMongoRepository testRepository;

    @Autowired
    private ReportMongoRepository reportMongoRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        testRepository.deleteAll();
        athleteRepository.deleteAll();
        reportMongoRepository.deleteAll();
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

    @Test
    void shouldSaveAndRetrieveAndDeleteReportIntegrationFlow() throws Exception {
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

        final ReportSaveRequestDTO saveRequest = ReportSaveRequestDTO.builder()
                .analysisType("COMPARISON")
                .athleteId(athleteId)
                .testIdA(savedTestA.getId())
                .testIdB(savedTestB.getId())
                .build();

        // 1. Save report
        final String savedReportJson = mockMvc.perform(post("/api/v1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportId").exists())
                .andExpect(jsonPath("$.athleteId").value(athleteId))
                .andExpect(jsonPath("$.testIds[0]").value(savedTestA.getId()))
                .andExpect(jsonPath("$.testIds[1]").value(savedTestB.getId()))
                .andExpect(jsonPath("$.payload.comparisonResults[0].delta").value("5.00"))
                .andReturn().getResponse().getContentAsString();

        final String reportId = objectMapper.readTree(savedReportJson).get("reportId").asText();

        // Verify DB contains it
        final ReportDocument reportInDb = reportMongoRepository.findById(reportId).orElse(null);
        assertThat(reportInDb).isNotNull();
        assertThat(reportInDb.getAthleteId()).isEqualTo(athleteId);

        // 2. Get reports by athlete
        mockMvc.perform(get("/api/v1/reports/athlete/{id}", athleteId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].reportId").value(reportId));

        // 3. Delete report
        mockMvc.perform(delete("/api/v1/reports/{id}", reportId))
                .andExpect(status().isNoContent());

        // Verify DB does not contain it anymore
        assertThat(reportMongoRepository.findById(reportId)).isEmpty();

        // 4. Delete report again -> returns 404
        mockMvc.perform(delete("/api/v1/reports/{id}", reportId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Report Not Found"));
    }
}
