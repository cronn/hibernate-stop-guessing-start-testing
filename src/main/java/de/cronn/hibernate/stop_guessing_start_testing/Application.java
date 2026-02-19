package de.cronn.hibernate.stop_guessing_start_testing;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PutExchange;

@SpringBootApplication
public class Application {

  static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  CommandLineRunner massDataCreator(PostRepository postRepository) {
    return _ -> {
      List<Post> posts = new ArrayList<>();
      Random random = new Random(0);
      Instant baseTime = Instant.parse("2025-01-01T00:00:00Z");

      // Arrays of sample data for generating realistic posts
      String[] topics = {
        "Spring Boot",
        "Hibernate",
        "JPA",
        "Microservices",
        "Docker",
        "Kubernetes",
        "REST API",
        "GraphQL",
        "Security",
        "Testing",
        "CI/CD",
        "Cloud Native",
        "Reactive Programming",
        "Performance",
        "Database",
        "Architecture",
        "Design Patterns",
        "Best Practices",
        "DevOps",
        "Monitoring"
      };
      String[] actions = {
        "Getting Started with",
        "Understanding",
        "Mastering",
        "Deep Dive into",
        "Complete Guide to",
        "Introduction to",
        "Advanced",
        "Optimizing",
        "Building with",
        "Deploying",
        "Scaling",
        "Debugging",
        "Troubleshooting",
        "Implementing",
        "Migrating to",
        "Working with",
        "Exploring",
        "Learning"
      };
      String[] commentTexts = {
        "Great article!",
        "Very helpful, thanks!",
        "Excellent explanation!",
        "I learned something new today",
        "This is exactly what I needed",
        "Clear and concise",
        "Love the examples",
        "Well written!",
        "Can you write more about this?",
        "Amazing content!",
        "Thanks for sharing",
        "Very informative",
        "Best tutorial I've found",
        "Bookmarked for later",
        "Helped me solve my problem",
        "Looking forward to more",
        "Please continue this series",
        "The code examples are perfect",
        "Finally understood this concept",
        "Brilliant explanation!"
      };

      // Generate 100 posts
      for (int i = 1; i <= 100; i++) {
        // Generate random post title
        String action = actions[random.nextInt(actions.length)];
        String topic = topics[random.nextInt(topics.length)];
        String title = action + " " + topic;

        // Add part number for some posts
        if (random.nextDouble() < 0.3) {
          title += " - Part " + (random.nextInt(5) + 1);
        }

        // Generate timestamp (spread over last year)
        long hoursToAdd = random.nextInt(365 * 24);
        Instant createdAt = baseTime.plusSeconds(hoursToAdd * 3600);

        Post post = new Post(title, createdAt);

        // Add random number of comments (0-5)
        int numComments = random.nextInt(6);
        for (int j = 0; j < numComments; j++) {
          String commentText = commentTexts[random.nextInt(commentTexts.length)];
          post.addPostComment(new PostComment(commentText));
        }

        posts.add(post);
      }

      // Save all posts in batch
      postRepository.saveAll(posts);
      System.out.println("Successfully created " + posts.size() + " posts with comments!");
    };
  }
}

record PostEntryDto(long id, String name, Instant createdAt, int numberOfComments) {}

record MinimalPostEntryDto(long id, String name, Instant createdAt) {}

@RestController
@HttpExchange("/posts")
class PostController {
  private final PostRepository postRepository;

  PostController(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @GetExchange
  public List<MinimalPostEntryDto> getPosts() {
    return postRepository.findAll().stream().map(this::mapToDto).toList();
  }

  private MinimalPostEntryDto mapToDto(Post post) {
    return new MinimalPostEntryDto(post.getId(), post.getName(), post.getCreatedAt());
  }

  @PutExchange("/{postId}")
  public void updatePost(@PathVariable long postId, @RequestBody MinimalPostEntryDto postEntryDto) {
    Post post = postRepository.findById(postId).orElseThrow();
    post.setCreatedAt(postEntryDto.createdAt());
    post.setName(postEntryDto.name());
    postRepository.save(post);
  }
}

interface PostRepository extends JpaRepository<Post, Long> {

  @Override
  @Query("SELECT post FROM Post post ORDER BY post.id")
  List<Post> findAll();
}

@Entity
class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq")
  @SequenceGenerator(name = "post_seq", allocationSize = 100)
  private Long id;

  @Version private Long version;

  @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "post", fetch = FetchType.LAZY)
  private List<PostComment> postComments = new ArrayList<>();

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Instant createdAt;

  public Post() {}

  public Post(String name, Instant createdAt) {
    this.name = name;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public Long getVersion() {
    return version;
  }

  public List<PostComment> getPostComments() {
    return postComments;
  }

  public void addPostComment(PostComment postComment) {
    postComment.setPost(this);
    postComments.add(postComment);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}

@Entity
class PostComment {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_comment_seq")
  @SequenceGenerator(name = "post_comment_seq", allocationSize = 100)
  private Long id;

  @Version private Long version;

  @ManyToOne(optional = false)
  private Post post;

  private String text;

  public PostComment(String text) {
    this.text = text;
  }

  public PostComment() {}

  public Long getVersion() {
    return version;
  }

  public Post getPost() {
    return post;
  }

  public Long getId() {
    return id;
  }

  public void setPost(Post post) {
    this.post = post;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
