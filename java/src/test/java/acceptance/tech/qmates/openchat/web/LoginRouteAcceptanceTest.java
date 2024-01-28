package acceptance.tech.qmates.openchat.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginRouteAcceptanceTest extends BaseOpenChatRouteAcceptanceTest{

    @Test
    void emptyCredentialsShouldReturn404() throws IOException, InterruptedException {
        HttpRequest request = requestBuilderFor("/login")
          .POST(bodyFor(Map.of(
            "username", "",
            "password", ""))).build();

        HttpResponse<String> response = send(request);
        assertEquals(404, response.statusCode());
        assertContentType("text/plain;charset=utf-8", response);
        assertEquals("Invalid credentials.", response.body());
    }
}
