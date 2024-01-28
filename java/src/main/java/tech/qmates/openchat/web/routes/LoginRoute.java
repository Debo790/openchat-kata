package tech.qmates.openchat.web.routes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class LoginRoute extends BaseRoute {

  @Override
  public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, Object> requestBody = stringJsonToMap(request.getInputStream());
    String username = (String) requestBody.get("username");
    String password = (String) requestBody.get("password");

    if (username.isBlank() || password.isBlank()) {
      textResponse(SC_NOT_FOUND, "Invalid credentials.", response);
    }
  }
}
