
package test;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import com.tomasulo.sample.IntegrationTest;
import com.tomasulo.sample.domain.User;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
public class UserRepositoryIntegrationTest {

    private static final int CONTAINER_PORT = 8000;
    private static final String CONTAINER_NAME = "devops";

    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
            .file("src/test/resources/docker-compose.yml")
            .projectName(ProjectName.random())
            .waitingForService(CONTAINER_NAME, HealthChecks.toHaveAllPortsOpen())
            .build();

    private static UserRepository repository;

    @BeforeClass
    public static void initialize() {
        DockerPort devops = docker.containers()
                .container(CONTAINER_NAME)
                .port(CONTAINER_PORT);
        String dynamoEndpoint = String.format("http://%s:%s", devops.getIp(), devops.getExternalPort());
        System.out.println(dynamoEndpoint);
        repository = new UserRepository(dynamoEndpoint);
    }

    @After
    public void tearDown() throws Exception {
        List<User> all = repository.getAll();
        all.forEach(user -> repository.delete(user));
    }

    @Test
    public void upsertOneUser() throws Exception {
        User user = randomUser();

        repository.upsert(user);

        assertThat(user).isEqualTo(repository.getById(user.getId()));
    }

    @Test
    public void getAllUsers() throws Exception {
        User userA = randomUser();
        User userB = randomUser();
        User userC = randomUser();

        repository.upsert(userA);
        repository.upsert(userB);
        repository.upsert(userC);

        assertThat(repository.getAll()).contains(userA, userB, userC);
    }

    @Test
    public void updateExistingUser() throws Exception {
        User user = randomUser();
        repository.upsert(user);
        user.setFirstName("Michael");
        user.setLastName("Jackson");

        repository.upsert(user);

        assertThat(user).isEqualTo(repository.getById(user.getId()));
        assertThat(user.getVersion()).isEqualTo(2);
    }

    @Test
    public void deleteOneUser() throws Exception {
        User user = randomUser();
        repository.upsert(user);

        repository.delete(user);

        assertThat(repository.getAll()).isEmpty();
    }

    private User randomUser() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setAge(new Random().nextInt(90));
        user.setFirstName(UUID.randomUUID().toString());
        user.setLastName(UUID.randomUUID().toString());
        return user;
    }
}