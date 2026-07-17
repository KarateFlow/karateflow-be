package com.karateflow.backend.dashboard.controller;

import com.karateflow.backend.dashboard.domain.model.DashboardSummaryResult;
import com.karateflow.backend.dashboard.usecase.GetDashboardSummaryUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetDashboardSummaryUseCase getDashboardSummaryUseCase;

    @Test
    void getSummary_returnsOk() throws Exception {
        DashboardSummaryResult result = DashboardSummaryResult.builder()
                .totalAthletes(10L)
                .totalTests(20L)
                .totalReports(5L)
                .recentTests(List.of())
                .recentReports(List.of())
                .build();

        when(getDashboardSummaryUseCase.getSummary()).thenReturn(result);

        try {
            mockMvc.perform(get("/api/v1/dashboard/summary"))
                    .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalAthletes").value(10))
                    .andExpect(jsonPath("$.totalTests").value(20))
                    .andExpect(jsonPath("$.totalReports").value(5));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
