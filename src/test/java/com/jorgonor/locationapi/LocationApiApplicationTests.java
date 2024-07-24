package com.jorgonor.locationapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(LocationApiTestContainersConfiguration.class)
@SpringBootTest(classes = {TestLocationApiApplication.class})
class LocationApiApplicationTests {

	@Test
	void contextLoads() {

	}

}
