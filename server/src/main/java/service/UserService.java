package service;


import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.exception.AlreadyTakenException;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.UserData;
import service.params.LoginRequest;
import service.params.LoginResult;
import service.params.RegisterRequest;


public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }


    public AuthData register(RegisterRequest registerRequest) throws DataAccessException, BadRequestException {
        String username = registerRequest.username();
        if (username == null || username.isBlank() ){
            throw new BadRequestException("Error: a username is required to set up an account!");
        }

        if ( userDAO.getUser(username) != null ){
            throw new AlreadyTakenException("Error: username already taken!");
        }
        else { // username isn't in system yet
            String password = registerRequest.password();
            if (password == null) { // no password given for user
                throw new BadRequestException("Error: a password is required!");
            }
            else{
                userDAO.addUser(new UserData(username, password, registerRequest.email()));
                return authDAO.createAuth(username);
            }
        }
    }


    public AuthData login(LoginRequest loginRequest) throws DataAccessException, BadRequestException {
        String username = loginRequest.username();
        if (username == null || username.isBlank() ){
            throw new BadRequestException("Error: a username is required to login!");
        }
        var userData = userDAO.getUser(username);

        if ( loginRequest.password().equals(userData.password()) ){
            String authToken = authDAO.createAuth(username).authToken();

            return new AuthData(username, authToken);
        }
        else { // Password Fail - Unauthorized
            throw new UnauthorizedResponse();
        }
    }


}
