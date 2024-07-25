package com.jorgonor.locationapi.db.integration;

import com.jorgonor.locationapi.LocationApiTestContainersConfiguration;
import com.jorgonor.locationapi.TestLocationApiApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(LocationApiTestContainersConfiguration.class)
@ActiveProfiles("mongo")
@SpringBootTest(classes = {TestLocationApiApplication.class})
public class MongoIntegrationTest extends PersistenceIntegrationTest {
}
