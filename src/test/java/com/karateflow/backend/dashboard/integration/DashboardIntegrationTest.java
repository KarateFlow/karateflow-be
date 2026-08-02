package com.karateflow.backend.dashboard.integration;

import com.karateflow.backend.athlete.persistence.document.AthleteDocument;
import com.karateflow.backend.athlete.persistence.repository.AthleteMongoRepository;
import com.karateflow.backend.dashboard.domain.model.DashboardSummaryResult;
import com.karateflow.backend.dashboard.usecase.GetDashboardSummaryUseCase;
import com.karateflow.backend.report.persistence.document.ReportDocument;
import com.karateflow.backend.report.persistence.repository.ReportMongoRepository;
import com.karateflow.backend.test.persistence.document.TestExecutionDocument;
import com.karateflow.backend.test.persistence.repository.TestExecutionMongoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class DashboardIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0").withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private AthleteMongoRepository athleteMongoRepository;

    @Autowired
    private TestExecutionMongoRepository testExecutionMongoRepository;

    @Autowired
    private ReportMongoRepository reportMongoRepository;

    @Autowired
    private GetDashboardSummaryUseCase getDashboardSummaryUseCase;

    @AfterEach
    void cleanUp() {
        athleteMongoRepository.deleteAll();
        testExecutionMongoRepository.deleteAll();
        reportMongoRepository.deleteAll();
    }

    @Test
    void shouldReturnCorrectDashboardSummary() {
        // Arrange
        // Athletes
        athleteMongoRepository.save(AthleteDocument.builder().firstName("A1").lastName("B1").build());
        athleteMongoRepository.save(AthleteDocument.builder().firstName("A2").lastName("B2").build());
        
        // Tests
        LocalDate now = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            testExecutionMongoRepository.save(TestExecutionDocument.builder()
                    .athleteId("a1")
                    .executionDate(now.minusDays(i))
                    .build());
        }

        // Reports
        LocalDateTime nowTime = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
        for (int i = 0; i < 6; i++) {
            reportMongoRepository.save(ReportDocument.builder()
                    .athleteId("a1")
                    .createdAt(nowTime.minusHours(i))
                    .build());
        }

        // Act
        DashboardSummaryResult result = getDashboardSummaryUseCase.getSummary();

        // Assert
        assertThat(result.getTotalAthletes()).isEqualTo(2);
        assertThat(result.getTotalTests()).isEqualTo(7);
        assertThat(result.getTotalReports()).isEqualTo(6);
        
        assertThat(result.getRecentTests()).hasSize(5);
        // The most recent should be the one minusDays(0)
        assertThat(result.getRecentTests().get(0).getExecutionDate()).isEqualTo(now);
        
        assertThat(result.getRecentReports()).hasSize(5);
        assertThat(result.getRecentReports().get(0).getCreatedAt()).isEqualTo(nowTime);
    }
}
