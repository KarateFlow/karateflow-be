package com.karateflow.backend.report.usecase;

@FunctionalInterface
public interface DeleteReportUseCase {
    void execute(String reportId);
}
