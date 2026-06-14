package com.karateflow.backend.athlete.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.karateflow.backend.athlete.dto.request.RecordAthleteRequest;
import com.karateflow.backend.athlete.dto.response.AthleteResponse;
import com.karateflow.backend.athlete.usecase.RecordAthleteUseCase;
import com.karateflow.backend.athlete.usecase.RetrieveAthletesUseCase;
import com.karateflow.backend.common.exception.AthleteAlreadyExistsException;
import com.karateflow.backend.common.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AthleteController.class)
@Import(GlobalExceptionHandler.class)
class AthleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private RecordAthleteUseCase recordUseCase;

    @MockitoBean
    private RetrieveAthletesUseCase retrieveUseCase;

    @Test
    void shouldRecordAthleteSuccessfully() throws Exception {
        // Given
        final RecordAthleteRequest request = RecordAthleteRequest.builder()
                .firstName("Mario")
                .lastName("Rossi")
                .birthDate(LocalDate.of(2010, 5, 15))
                .referenceContact("+39 333 1234567")
                .medicalNotes("None")
                .build();

        final AthleteResponse response = AthleteResponse.builder()
                .athleteId("123")
                .firstName("Mario")
                .lastName("Rossi")
                .birthDate(LocalDate.of(2010, 5, 15))
                .referenceContact("+39 333 1234567")
                .medicalNotes("None")
                .createdAt(LocalDateTime.now())
                .build();

        when(recordUseCase.execute(any(RecordAthleteRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.athleteId").value("123"))
                .andExpect(jsonPath("$.firstName").value("Mario"))
                .andExpect(jsonPath("$.lastName").value("Rossi"));
    }

    @Test
    void shouldReturnAthletesListSuccessfully() throws Exception {
        // Given
        final List<AthleteResponse> responseList = List.of(
                AthleteResponse.builder()
                        .athleteId("1")
                        .firstName("Mario")
                        .lastName("Rossi")
                        .birthDate(LocalDate.of(2010, 5, 15))
                        .build(),
                AthleteResponse.builder()
                        .athleteId("2")
                        .firstName("Luigi")
                        .lastName("Verdi")
                        .birthDate(LocalDate.of(2011, 6, 20))
                        .build()
        );

        when(retrieveUseCase.execute()).thenReturn(responseList);

        // When & Then
        mockMvc.perform(get("/api/v1/athletes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Mario"))
                .andExpect(jsonPath("$[1].firstName").value("Luigi"));
    }

    @Test
    void shouldReturnAthleteByIdSuccessfully() throws Exception {
        // Given
        final String athleteId = "123";
        final AthleteResponse response = AthleteResponse.builder()
                .athleteId(athleteId)
                .firstName("Mario")
                .lastName("Rossi")
                .birthDate(LocalDate.of(2010, 5, 15))
                .build();

        when(retrieveUseCase.execute(athleteId)).thenReturn(Optional.of(response));

        // When & Then
        mockMvc.perform(get("/api/v1/athletes/{athleteId}", athleteId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.athleteId").value(athleteId))
                .andExpect(jsonPath("$.firstName").value("Mario"));
    }

    @Test
    void shouldReturnNotFoundWhenAthleteDoesNotExist() throws Exception {
        // Given
        final String athleteId = "999";
        when(retrieveUseCase.execute(athleteId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/athletes/{athleteId}", athleteId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Athlete Not Found"));
    }

    @Test
    void shouldReturnBadRequestWhenMandatoryFieldsAreMissing() throws Exception {
        // Given
        final RecordAthleteRequest request = RecordAthleteRequest.builder()
                .firstName("") // Invalid: blank
                .lastName("Rossi")
                .birthDate(null) // Invalid: null
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenAthleteAlreadyExists() throws Exception {
        // Given
        final RecordAthleteRequest request = RecordAthleteRequest.builder()
                .firstName("Mario")
                .lastName("Rossi")
                .birthDate(LocalDate.of(2010, 5, 15))
                .referenceContact("+39 333 1234567")
                .medicalNotes("None")
                .build();

        when(recordUseCase.execute(any(RecordAthleteRequest.class)))
                .thenThrow(new AthleteAlreadyExistsException("Mario", "Rossi"));

        // When & Then
        mockMvc.perform(post("/api/v1/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Athlete Conflict"));
    }
}
