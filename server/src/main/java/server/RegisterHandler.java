package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import service.params.RegisterRequest;

public class RegisterHandler implements Handler {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        var registerRequest = new Gson().fromJson(context.body(), RegisterRequest.class);

        AuthData authResponse = userService.register(registerRequest);
        context.result(new Gson().toJson(authResponse));
    }
}
