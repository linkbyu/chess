package dataaccess;

import dataaccess.exception.DataAccessException;
import model.UserData;

public interface UserDAO {

    void addUser(UserData u) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void clear() throws DataAccessException;

}
