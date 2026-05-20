package service;

import dataaccess.*;
import dataaccess.exception.AlreadyTakenException;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void loginNonExistentUser(){

    }

    @Test
    void loginSuccess() throws DataAccessException {


    }

}
