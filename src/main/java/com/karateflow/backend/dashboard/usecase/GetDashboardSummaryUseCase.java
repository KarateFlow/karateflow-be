package com.karateflow.backend.dashboard.usecase;

import com.karateflow.backend.dashboard.domain.model.DashboardSummaryResult;

@FunctionalInterface
public interface GetDashboardSummaryUseCase {
    DashboardSummaryResult getSummary();
}
