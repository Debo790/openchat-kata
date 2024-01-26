package tech.qmates.openchat.web.routes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tech.qmates.openchat.AppFactory;
import tech.qmates.openchat.domain.usecase.GetAllUserUseCase;

import java.io.IOException;
import java.util.Map;

public class LoginRoute extends BaseRoute{

  @Override
  public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, Object> requestBody = stringJsonToMap(request.getInputStream());
    String username = (String) requestBody.get("username");
    String password = (String) requestBody.get("password");

    GetAllUserUseCase usecase = AppFactory.buildGetAllUserUseCase();
  }

}
