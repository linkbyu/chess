package server;

import dataaccess.exception.AlreadyTakenException;
import dataaccess.exception.BadRequestException;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

public class ExceptionHandler implements io.javalin.http.ExceptionHandler<Exception> {

    @Override
    public void handle(@NotNull Exception e, @NotNull Context ctx) {
        var builder = new StringBuilder();
        builder.append("{ \"message\": \"Error: ");

        switch(e) {
            case BadRequestException _:
                ctx.status(400);
                builder.append("bad request");
                break;
            case UnauthorizedResponse _:
                ctx.status(401);
                builder.append("unauthorized");
                break;
            case AlreadyTakenException _:
                ctx.status(403);
                builder.append("already taken");
                break;
            default:
                ctx.status(500);
                builder.append(e.getMessage());
        }
        builder.append("\" }");
        ctx.json(builder.toString());
    }
}
