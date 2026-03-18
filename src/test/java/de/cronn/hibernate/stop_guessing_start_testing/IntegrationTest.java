package de.cronn.hibernate.stop_guessing_start_testing;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

class IntegrationTest extends AbstractSpringBootTest {

  @Test
  void contextLoads() {}

  @Test
  void testGetPosts() {
    var posts = captureQueryAndCompareWithFile(this::getPosts);
    assertWithJsonFile(posts);
  }

  public List<Object> getPosts() {
    return restTestClient
        .get()
        .uri("/posts")
        .exchangeSuccessfully()
        .expectBody(new ParameterizedTypeReference<List<Object>>() {})
        .returnResult()
        .getResponseBody();
  }

  @Test
  void testUpdatePost() {
    captureQueryAndCompareWithFile(
        () ->
            updatePost(
                new MinimalPostEntryDto(1, "New Name", Instant.parse("2025-02-02T12:00:00Z"))));
  }

  public void updatePost(MinimalPostEntryDto postEntryDto) {
    restTestClient
        .put()
        .uri("/posts/{postId}", postEntryDto.id())
        .body(postEntryDto)
        .exchangeSuccessfully();
  }
}
