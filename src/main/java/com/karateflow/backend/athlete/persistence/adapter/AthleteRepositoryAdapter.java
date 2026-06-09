package com.karateflow.backend.athlete.persistence.adapter;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.athlete.mapper.AthleteMapper;
import com.karateflow.backend.athlete.persistence.document.AthleteDocument;
import com.karateflow.backend.athlete.persistence.repository.AthleteMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AthleteRepositoryAdapter implements AthleteRepository {

    private final AthleteMongoRepository mongoRepository;
    private final AthleteMapper athleteMapper;

    @Override
    public Athlete save(final Athlete athlete) {
        final AthleteDocument document = athleteMapper.toDocument(athlete);
        final AthleteDocument savedDocument = mongoRepository.save(document);
        return athleteMapper.toDomain(savedDocument);
    }

    @Override
    public Optional<Athlete> findById(final String athleteId) {
        return mongoRepository.findById(athleteId)
                .map(athleteMapper::toDomain);
    }

    @Override
    public List<Athlete> findAll() {
        return mongoRepository.findAll().stream()
                .map(athleteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(final String athleteId) {
        mongoRepository.deleteById(athleteId);
    }
}
