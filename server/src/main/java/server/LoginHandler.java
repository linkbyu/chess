package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import service.params.LoginRequest;

public class LoginHandler implements Handler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        var loginRequest = new Gson().fromJson(context.body(), LoginRequest.class);

        AuthData authResponse = userService.login(loginRequest);
        context.result(new Gson().toJson(authResponse));
    }
}
