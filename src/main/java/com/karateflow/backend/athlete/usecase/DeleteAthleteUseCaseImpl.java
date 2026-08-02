package com.karateflow.backend.athlete.usecase;

import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.common.exception.AthleteNotFoundException;
import com.karateflow.backend.report.domain.port.ReportRepository;
import com.karateflow.backend.test.domain.port.TestExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("PMD.LongVariable")
public class DeleteAthleteUseCaseImpl implements DeleteAthleteUseCase {

    private final AthleteRepository athleteRepository;
    private final TestExecutionRepository testExecutionRepository;
    private final ReportRepository reportRepository;

    @Override
    @Transactional
    public void execute(final String athleteId) {
        log.info("Executing transactional cascade delete for athlete ID: {}", athleteId);
        
        if (athleteRepository.findById(athleteId).isEmpty()) {
            throw new AthleteNotFoundException("Athlete not found with ID: " + athleteId);
        }

        log.debug("Deleting reports for athlete ID: {}", athleteId);
        reportRepository.deleteByAthleteId(athleteId);
        
        log.debug("Deleting test executions for athlete ID: {}", athleteId);
        testExecutionRepository.deleteByAthleteId(athleteId);
        
        log.debug("Deleting athlete ID: {}", athleteId);
        athleteRepository.deleteById(athleteId);
        
        log.info("Cascade delete completed successfully for athlete ID: {}", athleteId);
    }
}
