package service;


import dataaccess.exception.*;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import model.params.LoginRequest;
import model.params.RegisterRequest;


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
            throw new BadRequestException("a username is required to set up an account!");
        }

        if ( userDAO.getUser(username) != null ){
            throw new AlreadyTakenException("username already taken!");
        }
        else { // username isn't in system yet
            String password = registerRequest.password();
            if ( password == null || password.isBlank() ) { // no password given for user
                throw new BadRequestException("a password is required!");
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
            throw new BadRequestException("a username is required to login!");
        }

        var userData = userDAO.getUser(username);
        if (userData == null) { // No such user exists
            throw new UnauthorizedException("invalid username");
        }


        String givenPassword = loginRequest.password();
        if ( givenPassword == null || givenPassword.isBlank() ) { // No given password
            throw new BadRequestException("no password given");
        }
        if ( !verifyPassword(username, givenPassword) ) { // Password Fail - Unauthorized
            throw new UnauthorizedException("invalid password");
        }
        else { // Password Success
            String authToken = authDAO.createAuth(username).authToken();

            return new AuthData(username, authToken);
        }
    }

    boolean verifyPassword(String username, String providedClearTextPassword) throws DataAccessException {
        // read the previously hashed password from the database
        var user = userDAO.getUser(username);
        var hashedPassword = user.password();

        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    public AuthData verifyAuth(String authToken) throws DataAccessException, UnauthorizedException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null){
            throw new UnauthorizedException("Authorization doesn't exist");
        }
        return authData;
    }

    public void logout(String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }


}
