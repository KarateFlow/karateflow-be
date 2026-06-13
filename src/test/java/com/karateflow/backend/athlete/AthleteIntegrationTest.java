package com.karateflow.backend.athlete;

import com.karateflow.backend.BaseIntegrationTest;
import com.karateflow.backend.athlete.persistence.document.AthleteDocument;
import com.karateflow.backend.athlete.persistence.repository.AthleteMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AthleteIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AthleteMongoRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
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
}
