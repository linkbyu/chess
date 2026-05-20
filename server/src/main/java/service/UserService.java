package service;


import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import dataaccess.AuthDAO;
import service.params.LoginRequest;
import service.params.LoginResult;


public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;


    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }


    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        String username = loginRequest.username();
        UserData userData = userDAO.getUser(username);

        if ( loginRequest.password().equals(userData.password()) ){
            String authToken = authDAO.createAuth(username).authToken();

            return new LoginResult(null, username, authToken);
        }
        else { // Password Fail - Unauthorized
            return new LoginResult(null, null, null);
        }
    }


}
