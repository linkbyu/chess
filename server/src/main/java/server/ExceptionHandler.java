package server;

import dataaccess.exception.AlreadyTakenException;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.UnauthorizedException;
import dataaccess.exception.UserNullException;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public class ExceptionHandler implements io.javalin.http.ExceptionHandler<Exception> {

    @Override
    public void handle(@NotNull Exception e, @NotNull Context ctx) {
        var builder = new StringBuilder();
        builder.append("{ \"message\": \"Error: ");

        switch(e) {
            case BadRequestException badE:
                ctx.status(400);
                builder.append(e.getMessage());
                break;
            case UnauthorizedException bad:
                ctx.status(401);
                builder.append(e.getMessage());
                break;
            case UserNullException bad:
                ctx.status(401);
                builder.append(e.getMessage());
                break;
            case AlreadyTakenException bad:
                ctx.status(403);
                builder.append(e.getMessage());
                break;
            default:
                ctx.status(500);
                builder.append(e.getMessage());
        }
        builder.append("\" }");
        ctx.json(builder.toString());
    }
}
