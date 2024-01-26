package acceptance.tech.qmates.openchat.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginRouteAcceptanceTest extends BaseOpenChatRouteAcceptanceTest{

    @Test
    void questoEIlPrimoTest() throws IOException, InterruptedException {

        HttpRequest request = requestBuilderFor("/login").POST(bodyFor(Map.of("", ""))).build();

        HttpResponse<String> response = send(request);
        assertEquals(200, response.statusCode());

    }

}
