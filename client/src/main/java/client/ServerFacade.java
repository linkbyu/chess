package client;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;

import model.GameCatalog;
import model.params.*;

public class ServerFacade {
    private static final int TIMEOUT_MILLIS = 5000;

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;


    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AuthData register(RegisterRequest registerRequest) throws ResponseException {
        var request = buildRequest("POST", "/user", null, registerRequest);
        var response = sendRequest(request);

        return handleResponse(response, AuthData.class);
    }

    public AuthData login(LoginRequest loginRequest) throws ResponseException {
        var request = buildRequest("POST", "/session", null, loginRequest);
        var response = sendRequest(request);

        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "/session", authToken, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }


    public GameCatalog listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", authToken, null);
        var response = sendRequest(request);
        return handleResponse(response, GameCatalog.class);
    }


    public CreateResult createGame(String authToken, CreateRequest createRequest) throws ResponseException {
        var request = buildRequest("POST", "/game", authToken, createRequest);
        var response = sendRequest(request);

        return handleResponse(response, CreateResult.class); // return gameID as a String
    }

    public void joinGame(String authToken, JoinRequest joinRequest) throws ResponseException {
        var request = buildRequest("PUT", "/game", authToken, joinRequest);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clearDatabase() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        sendRequest(request);
    }



    private HttpRequest buildRequest(String method, String path, String authToken, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                //.timeout(java.time.Duration.ofMillis(TIMEOUT_MILLIS))
                .method(method, makeRequestBody(body));

        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request) );
        }
        else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try{
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                var map = new Gson().fromJson(body, HashMap.class);
                String message = map.get("message").toString();

                throw new ResponseException(ResponseException.fromHttpStatusCode(status), message);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return (status / 100) == 2;
    }

    public String getServerUrl() {
        return serverUrl;
    }
}
