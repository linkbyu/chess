package service;


import dataaccess.exception.AlreadyTakenException;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.exception.UnauthorizedException;
import model.AuthData;
import model.UserData;
import service.params.LoginRequest;
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
            if ( password == null || password.isBlank() ) { // no password given for user
                throw new BadRequestException("Error: a password is required!");
            }
            else{
                userDAO.addUser(new UserData(username, password, registerRequest.email()));
                return authDAO.createAuth(username);
            }
        }
    }


    public AuthData login(LoginRequest loginRequest)
            throws DataAccessException, BadRequestException, UnauthorizedException {
        String username = loginRequest.username();
        if (username == null || username.isBlank() ){ // not a valid username
            throw new BadRequestException("Error: a username is required to login!");
        }

        var userData = userDAO.getUser(username);
        if ( userData == null ){ // No such user exists
            throw new UnauthorizedException("Error: invalid username");
        }


        String givenPassword = loginRequest.password();
        if ( givenPassword == null || givenPassword.isBlank() ) { // No given password
            throw new BadRequestException("Error: no password given");
        }
        if ( !givenPassword.equals(userData.password()) ) { // Password Fail - Unauthorized
            throw new UnauthorizedException("Error: invalid password");
        }
        else { // Password Success
            String authToken = authDAO.createAuth(username).authToken();

            return new AuthData(username, authToken);
        }
    }

    public void verifyAuth(String authToken) throws DataAccessException, UnauthorizedException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null){
            throw new UnauthorizedException("Error: Authorization doesn't exist");
        }
    }

    public void logout(String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }


}
