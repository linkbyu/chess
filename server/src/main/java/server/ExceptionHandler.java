package server;

import dataaccess.exception.AlreadyTakenException;
import dataaccess.exception.BadRequestException;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

public class ExceptionHandler implements io.javalin.http.ExceptionHandler<Exception> {

    @Override
    public void handle(@NotNull Exception e, @NotNull Context ctx) {
        ctx.json("{ \"message\": \"Error: ");

        switch(e) {
            case BadRequestException _:
                ctx.status(400);
                ctx.json("bad request");
                break;
            case UnauthorizedResponse _:
                ctx.status(401);
                ctx.json("unauthorized");
            case AlreadyTakenException _:
                ctx.status(403);
                ctx.json("already taken");
            default:
                ctx.status(500);
                ctx.json(e.getMessage());
        }
        ctx.json("\" }");
    }
}
