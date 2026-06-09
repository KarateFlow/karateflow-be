package com.karateflow.backend;

import com.karateflow.backend.athlete.persistence.repository.AthleteMongoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@EnableAutoConfiguration(excludeName = {
		"org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
		"org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration"
})
class BackendApplicationTests {

	@MockitoBean
	private AthleteMongoRepository athleteMongoRepository;

	@Test
	void contextLoads() {
	}

}
