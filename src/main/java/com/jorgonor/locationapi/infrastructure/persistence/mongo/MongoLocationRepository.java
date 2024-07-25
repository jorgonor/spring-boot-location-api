package com.jorgonor.locationapi.infrastructure.persistence.mongo;

import com.jorgonor.locationapi.domain.Location;
import com.jorgonor.locationapi.domain.LocationId;
import com.jorgonor.locationapi.domain.exception.EntityNotFoundException;
import com.jorgonor.locationapi.domain.repository.LocationRepository;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Repository
@Profile("mongo")
@Slf4j
public class MongoLocationRepository implements LocationRepository {

    private static final int MAX_ATTEMPTS = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final MongoTemplate mongoTemplate;
    private final LocationMapper locationMapper;

    public MongoLocationRepository(
        MongoTemplate mongoTemplate,
        LocationMapper locationMapper) {
        this.mongoTemplate = mongoTemplate;
        this.locationMapper = locationMapper;
    }

    @Override
    public Location get(LocationId id) {
        LocationDocument locationDocument = mongoTemplate.findById(id.getId(), LocationDocument.class);
        if (locationDocument == null) {
            throw new EntityNotFoundException("Entity with id " + id.toString() + " not found");
        }

        return locationMapper.mapToLocation(locationDocument);
    }

    @Override
    public Location save(Location location) {

        LocationDocument locationDocument = locationMapper.mapToLocationDocument(location);

        locationDocument.setCreatedAt(getCurrentInstant());
        locationDocument.setModifiedAt(getCurrentInstant());

        LocationDocument savedLocationDocument = null;
        int attempt = 0;

        do {
            locationDocument.setId(getNextEntityId());
            try {
                savedLocationDocument = mongoTemplate.insert(locationDocument);
            } catch (DuplicateKeyException e) {
                log.debug("Key collision {}. Running attempt {} out of {} attempts.", locationDocument.getId(), attempt, MAX_ATTEMPTS);
                savedLocationDocument = null;
            }
            attempt++;
        } while (attempt < MAX_ATTEMPTS && savedLocationDocument == null);

        // last attempt that would throw an exception
        if (savedLocationDocument == null) {
            locationDocument.setId(getNextEntityId());
            savedLocationDocument = mongoTemplate.insert(locationDocument);
        }

        return locationMapper.mapToLocation(savedLocationDocument);
    }

    @Override
    public Location update(LocationId id, Location location) {

        LocationDocument locationDocument = mongoTemplate.findById(id.getId(), LocationDocument.class);
        if (locationDocument == null) {
            throw new EntityNotFoundException("Entity with id " + id.toString() + " not found");
        }

        locationMapper.remapLocationDocument(locationDocument, location);
        locationDocument.setId(id.getId());
        locationDocument.setModifiedAt(getCurrentInstant());
        LocationDocument savedLocationDocument = mongoTemplate.save(locationDocument);
        return locationMapper.mapToLocation(savedLocationDocument);
    }

    @Override
    public boolean delete(LocationId id) {
        DeleteResult deleteResult = mongoTemplate.remove(getLocationIdQuery(id), LocationDocument.class);
        return deleteResult.getDeletedCount() == 1;
    }

    private Query getLocationIdQuery(LocationId locationId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(locationId.getId()));

        return query;
    }

    private static Instant getCurrentInstant() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

    /**
     * Helper method to share a basic in-memory java sequence for the mongo documents
     * @return
     */
    private static Long getNextEntityId() {
        return Math.abs(RANDOM.nextLong());
    }
}
