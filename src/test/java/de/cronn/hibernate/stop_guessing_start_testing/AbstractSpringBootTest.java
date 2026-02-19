package de.cronn.hibernate.stop_guessing_start_testing;

import de.cronn.hibernate.stop_guessing_start_testing.test.QueryAndParamsCapturingListener;
import de.cronn.hibernate.stop_guessing_start_testing.test.QueryValidationTraits;
import de.cronn.hibernate.stop_guessing_start_testing.test.ResponseEntityValidationFileAssertionTraits;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@ExtendWith(SoftAssertionsExtension.class)
public class AbstractSpringBootTest
    implements ResponseEntityValidationFileAssertionTraits, QueryValidationTraits {
  @Autowired protected RestTestClient restTestClient;
  @Autowired protected ObjectMapper objectMapper;
  @Autowired private QueryAndParamsCapturingListener queryAndParamsCapturingListener;

  @InjectSoftAssertions protected SoftAssertions softly;

  @Override
  public ObjectMapper objectMapper() {
    return objectMapper;
  }

  @Override
  public QueryAndParamsCapturingListener getQueryListener() {
    return queryAndParamsCapturingListener;
  }

  @DynamicPropertySource
  static void testContainer(DynamicPropertyRegistry registry) {
    PostgreSQLContainer postgreSQLContainer =
        new PostgreSQLContainer("postgres:latest").withReuse(true);
    postgreSQLContainer.start();

    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
  }

  @Override
  public FailedAssertionHandler failedAssertionHandler() {
    return callable -> softly.check(callable::call);
  }
}
