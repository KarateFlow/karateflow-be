package com.karateflow.backend.report.usecase;

import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.common.exception.AthleteNotFoundException;
import com.karateflow.backend.report.domain.port.ReportRepository;
import com.karateflow.backend.report.dto.response.ReportResponseDTO;
import com.karateflow.backend.report.mapper.ReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetrieveReportsUseCaseImpl implements RetrieveReportsUseCase {

    private final ReportRepository reportRepository;
    private final AthleteRepository athleteRepository;
    private final ReportMapper mapper;

    @Override
    public List<ReportResponseDTO> execute(final String athleteId) {
        if (athleteRepository.findById(athleteId).isEmpty()) {
            throw new AthleteNotFoundException("Athlete not found with ID: " + athleteId);
        }

        return reportRepository.findByAthleteId(athleteId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
