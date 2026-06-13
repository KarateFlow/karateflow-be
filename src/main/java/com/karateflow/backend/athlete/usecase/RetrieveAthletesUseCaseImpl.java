package com.karateflow.backend.athlete.usecase;

import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.athlete.dto.response.AthleteResponse;
import com.karateflow.backend.athlete.mapper.AthleteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetrieveAthletesUseCaseImpl implements RetrieveAthletesUseCase {

    private final AthleteRepository athleteRepository;
    private final AthleteMapper athleteMapper;

    @Override
    public List<AthleteResponse> execute() {
        return athleteRepository.findAll().stream()
                .map(athleteMapper::toResponse)
                .collect(Collectors.toList());
    }
}
