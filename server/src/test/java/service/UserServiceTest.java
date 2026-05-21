package service;

import dataaccess.*;
import dataaccess.exception.AlreadyTakenException;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.UnauthorizedException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.params.LoginRequest;
import service.params.RegisterRequest;

public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() throws DataAccessException, BadRequestException {
        userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

        userService.register(new RegisterRequest("ben", "abc", "laa@gmail.com"));
    }

    @Test
    void registerNullUsername(){
        Assertions.assertThrows(BadRequestException.class, () ->
                userService.register(new RegisterRequest("", "", "")));
    }

    @Test
    void registerUsernameAlreadyTaken(){
        Assertions.assertThrows(AlreadyTakenException.class, () ->
                userService.register(new RegisterRequest("ben", "xyz", "xyz@yahoo.com")));
    }

    @Test
    void registerSuccess() throws DataAccessException, BadRequestException {
        String username = "toby123";

        Assertions.assertEquals(username,
                userService.register(new RegisterRequest(username, "secret", null)).username());
    }


    @Test
    void loginInvalidUsername() {
        String username = "";
        Assertions.assertThrows(BadRequestException.class, () ->
                userService.login(new LoginRequest(username, "secret")));
    }

    @Test
    void loginNonExistentUser() {
        Assertions.assertThrows(UnauthorizedException.class, () ->
                userService.login(new LoginRequest("username", null)));
    }

    @Test
    void loginSuccess() throws DataAccessException, BadRequestException, UnauthorizedException {
        Assertions.assertEquals("ben",
                userService.login(new LoginRequest("ben", "abc")).username());
    }

    @Test
    void verifyInvalidAuth(){
        Assertions.assertThrows(UnauthorizedException.class, () ->
                userService.verifyAuth("pleaseletmeinIwannaplaychess"));
    }

    @Test
    void verifyAuthSuccess() throws BadRequestException, DataAccessException, UnauthorizedException {
        var authData = userService.register(new RegisterRequest("paul5", "abc", "laa@gmail.com"));
        String authToken = authData.authToken();
        Assertions.assertEquals(userService.verifyAuth(authToken), authData);
    }

    @Test
    void logoutSuccess() throws BadRequestException, DataAccessException {
        AuthData auth = userService.register(new RegisterRequest("paul5", "abc", "laa@gmail.com"));
        String authToken = auth.authToken();
        userService.logout(authToken);

        Assertions.assertThrows(UnauthorizedException.class, () ->
                userService.verifyAuth(authToken));
    }

}
