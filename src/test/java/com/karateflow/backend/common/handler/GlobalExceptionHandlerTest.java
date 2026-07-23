package com.karateflow.backend.common.handler;

import com.karateflow.backend.common.exception.AthleteAlreadyExistsException;
import com.karateflow.backend.common.exception.AthleteNotFoundException;
import com.karateflow.backend.common.exception.ReportNotFoundException;
import com.karateflow.backend.common.exception.TestExecutionNotFoundException;
import com.karateflow.backend.common.exception.TestTemplateNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test class per verificare i casi gestiti da GlobalExceptionHandler.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    /**
     * Testa il caso di gestione dell'eccezione AthleteNotFoundException (Sad path).
     */
    @Test
    void handleAthleteNotFoundException() {
        // Arrange
        AthleteNotFoundException ex = new AthleteNotFoundException("Athlete not found");
        
        // Act
        ProblemDetail result = handler.handleAthleteNotFoundException(ex);

        // Assert
        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Athlete Not Found");
        assertThat(result.getType()).isEqualTo(URI.create("https://karateflow.com/errors/athlete-not-found"));
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    /**
     * Testa il caso di gestione dell'eccezione TestExecutionNotFoundException (Sad path).
     */
    @Test
    void handleTestExecutionNotFoundException() {
        // Arrange
        TestExecutionNotFoundException ex = new TestExecutionNotFoundException("Execution not found");
        
        // Act
        ProblemDetail result = handler.handleTestExecutionNotFoundException(ex);

        // Assert
        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Test Execution Not Found");
        assertThat(result.getType()).isEqualTo(URI.create("https://karateflow.com/errors/test-execution-not-found"));
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    /**
     * Testa il caso di gestione dell'eccezione TestTemplateNotFoundException (Sad path).
     */
    @Test
    void handleTestTemplateNotFoundException() {
        // Arrange
        TestTemplateNotFoundException ex = new TestTemplateNotFoundException("Template not found");
        
        // Act
        ProblemDetail result = handler.handleTestTemplateNotFoundException(ex);

        // Assert
        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Test Template Not Found");
        assertThat(result.getType()).isEqualTo(URI.create("https://karateflow.com/errors/test-template-not-found"));
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    /**
     * Testa il caso di gestione dell'eccezione ReportNotFoundException (Sad path).
     */
    @Test
    void handleReportNotFoundException() {
        // Arrange
        ReportNotFoundException ex = new ReportNotFoundException("Report not found");
        
        // Act
        ProblemDetail result = handler.handleReportNotFoundException(ex);

        // Assert
        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Report Not Found");
        assertThat(result.getType()).isEqualTo(URI.create("https://karateflow.com/errors/report-not-found"));
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    /**
     * Testa il caso di gestione dell'eccezione AthleteAlreadyExistsException (Sad path).
     */
    @Test
    void handleAthleteAlreadyExistsException_withAthleteAlreadyExistsException() {
        // Arrange
        AthleteAlreadyExistsException ex = new AthleteAlreadyExistsException("Mario", "Rossi");
        
        // Act
        ProblemDetail result = handler.handleAthleteAlreadyExistsException(ex);

        // Assert
        assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(result.getTitle()).isEqualTo("Athlete Conflict");
        assertThat(result.getType()).isEqualTo(URI.create("https://karateflow.com/errors/athlete-already-exists"));
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    /**
     * Testa il caso di gestione dell'eccezione DuplicateKeyException legata ad atleti (Sad path).
     */
    @Test
    void handleAthleteAlreadyExistsException_withDuplicateKeyException() {
        // Arrange
        DuplicateKeyException ex = new DuplicateKeyException("Duplicate key");
        
        // Act
        ProblemDetail result = handler.handleAthleteAlreadyExistsException(ex);

        // Assert
        assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(result.getTitle()).isEqualTo("Athlete Conflict");
        assertThat(result.getType()).isEqualTo(URI.create("https://karateflow.com/errors/athlete-already-exists"));
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    /**
     * Testa il caso di gestione degli errori di validazione dei parametri della request (Sad path).
     */
    @Test
    void handleValidationException() {
        // Arrange
        MethodParameter parameter = mock(MethodParameter.class);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "field", "must not be null"));
        bindingResult.addError(new FieldError("objectName", "field2", "must be valid"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);
        
        // Act
        ProblemDetail result = handler.handleValidationException(ex);

        // Assert
        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getTitle()).isEqualTo("Invalid Request Content");
        assertThat(result.getDetail()).contains("field: must not be null");
        assertThat(result.getDetail()).contains("field2: must be valid");
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    /**
     * Testa il caso di gestione della mancanza di parametri nella request (Sad path).
     */
    @Test
    void handleMissingServletRequestParameterException() {
        // Arrange
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("param", "String");
        
        // Act
        ProblemDetail result = handler.handleMissingServletRequestParameterException(ex);

        // Assert
        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getTitle()).isEqualTo("Missing Request Parameter");
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    /**
     * Testa il caso di gestione di un'eccezione generica per errori server interni (Sad path).
     */
    @Test
    void handleGeneralException() {
        // Arrange
        Exception ex = new Exception("General error");
        
        // Act
        ProblemDetail result = handler.handleGeneralException(ex);

        // Assert
        assertThat(result.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getProperties()).containsKey("timestamp");
    }
}
