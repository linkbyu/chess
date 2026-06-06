package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();


    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        try {
            UserGameCommand userCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (userCommand.getCommandType()) {
                case CONNECT -> enter(ctx.session);
                case MAKE_MOVE -> makeMove(ctx.session);
                case RESIGN -> resign(ctx.session);
                case LEAVE -> leave(ctx.session);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) {
        System.out.println("Websocket closed");
    }

    private void enter(Session session) {

    }

    private void makeMove(Session session) {

    }

    private void resign(Session session) {

    }

    private void leave(Session session) {

    }
}
