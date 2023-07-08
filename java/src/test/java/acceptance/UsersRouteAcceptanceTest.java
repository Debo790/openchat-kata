package acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersRouteAcceptanceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        httpClient.send(requestBuilderFor("/admin").DELETE().build(), HttpResponse.BodyHandlers.discarding());
    }

    @Test
    void retrieveEmptyUserListWithNoRegisteredUser() throws IOException, InterruptedException {
        HttpRequest request = requestBuilderFor("/users").GET().build();

        HttpResponse<String> response = send(request);

        assertEquals(200, response.statusCode());
        assertEquals("application/json", response.headers().firstValue("Content-Type").get());
        assertEquals("[]", response.body());
    }

    @Test
    void registerSomeUsersAndRetrieveThem() throws IOException, InterruptedException {
        HttpRequest request = requestBuilderFor("/users")
            .POST(bodyFor(new HashMap<>() {{
                put("username", "alice90");
                put("password", "pass1234");
                put("about", "About alice user.");
            }}))
            .build();

        HttpResponse<String> response = send(request);

        assertEquals(201, response.statusCode());
        assertEquals("application/json", response.headers().firstValue("Content-Type").get());
        Map<String, Object> responseBody = stringJsonToMap(response.body());
        assertEquals("alice90", responseBody.get("username"));
        assertEquals("About alice user.", responseBody.get("about"));
        String aliceUUID = (String) responseBody.get("id");
        assertDoesNotThrow(() -> UUID.fromString(aliceUUID));

        // ========================================= register another user

        response = send(requestBuilderFor("/users")
            .POST(bodyFor(new HashMap<>() {{
                put("username", "john91");
                put("password", "pass4321");
                put("about", "About john user.");
            }})).build()
        );
        assertEquals(201, response.statusCode());
        responseBody = stringJsonToMap(response.body());
        String johnUUID = (String) responseBody.get("id");

        // ========================================= retrieve registered users

        HttpRequest retrieveUsersRequest = requestBuilderFor("/users").GET().build();

        HttpResponse<String> retrieveUsersResponse = send(retrieveUsersRequest);

        assertEquals(200, retrieveUsersResponse.statusCode());
        assertEquals("application/json", retrieveUsersResponse.headers().firstValue("Content-Type").get());
        List<Map<String, Object>> retrieveUsersResponseBody = stringJsonArrayToList(retrieveUsersResponse.body());
        assertEquals(2, retrieveUsersResponseBody.size());
        assertThat(retrieveUsersResponseBody).anySatisfy(userMap -> {
            assertEquals("alice90", userMap.get("username"));
            assertEquals("About alice user.", userMap.get("about"));
        });
        assertThat(retrieveUsersResponseBody).anySatisfy(userMap -> {
            assertEquals("john91", userMap.get("username"));
            assertEquals("About john user.", userMap.get("about"));
        });
    }

    @Test
    void usernameAlreadyInUse() throws IOException, InterruptedException {
        HttpResponse<String> firstRegistrationResponse = send(requestBuilderFor("/users")
            .POST(bodyFor(new HashMap<>() {{
                put("username", "bob89");
                put("password", "123pass");
                put("about", "About bob user.");
            }})).build());
        assertEquals(201, firstRegistrationResponse.statusCode());

        HttpResponse<String> secondAttemptResponse = send(requestBuilderFor("/users")
            .POST(bodyFor(new HashMap<>() {{
                put("username", "bob89");
                put("password", "pass123");
                put("about", "Another about.");
            }})).build());
        assertEquals(400, secondAttemptResponse.statusCode());
        assertEquals("text/plain;charset=utf-8", secondAttemptResponse.headers().firstValue("Content-Type").get());
        assertEquals("Username already in use.", secondAttemptResponse.body());
    }

    private HttpRequest.BodyPublisher bodyFor(Object requestBody) throws JsonProcessingException {
        return HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody));
    }

    private static HttpRequest.Builder requestBuilderFor(String route) {
        return HttpRequest.newBuilder().uri(URI.create("http://localhost:8000" + route));
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    //@formatter:off
    private Map<String, Object> stringJsonToMap(String body) throws IOException {
        TypeReference<Map<String, Object>> targetType = new TypeReference<>() { };
        return objectMapper.readValue(body, targetType);
    }

    private List<Map<String, Object>> stringJsonArrayToList(String body) throws JsonProcessingException {
        TypeReference<List<Map<String, Object>>> targetType = new TypeReference<>() { };
        return objectMapper.readValue(body, targetType);
    }
    //@formatter:on

}
