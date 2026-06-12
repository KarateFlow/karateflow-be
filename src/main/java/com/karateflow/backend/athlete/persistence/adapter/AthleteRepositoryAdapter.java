package com.karateflow.backend.athlete.persistence.adapter;

import com.karateflow.backend.athlete.domain.model.Athlete;
import com.karateflow.backend.athlete.domain.port.AthleteRepository;
import com.karateflow.backend.athlete.mapper.AthleteMapper;
import com.karateflow.backend.athlete.persistence.document.AthleteDocument;
import com.karateflow.backend.athlete.persistence.repository.AthleteMongoRepository;
import com.karateflow.backend.common.exception.AthleteAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
        mongoRepository.findByFirstNameAndLastName(athlete.getFirstName(), athlete.getLastName())
                .ifPresent(existing -> {
                    throw new AthleteAlreadyExistsException(athlete.getFirstName(), athlete.getLastName());
                });

        AthleteDocument document = athleteMapper.toDocument(athlete);
        if (document.getAthleteId() == null) {
            document.setAthleteId(new ObjectId().toHexString());
        }
        if (document.getCreatedAt() == null) {
            document.setCreatedAt(LocalDateTime.now());
        }

        AthleteDocument savedDocument = mongoRepository.save(document);
        return athleteMapper.toDomain(savedDocument);
    }

    @Override
    public Optional<Athlete> findById(final String athleteId) {
        return mongoRepository.findById(athleteId)
                .map(athleteMapper::toDomain);
    }

    @Override
    public Optional<Athlete> findByFirstNameAndLastName(final String firstName, final String lastName) {
        return mongoRepository.findByFirstNameAndLastName(firstName, lastName)
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
