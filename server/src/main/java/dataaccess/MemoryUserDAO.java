package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;


public class MemoryUserDAO implements UserDAO{
    private final Collection<UserData> userList;

    public MemoryUserDAO() {
        userList = new ArrayList<>();
    }


    @Override
    public void insertUser(UserData u) throws DataAccessException {
        userList.add(u);
    }

    @Override
    public UserData getUser(String username) throws UserNullException {
        try{
            for (UserData u : userList){
                if ( username.equals(u.username()) ){
                    return u;
                }
            }
        } catch (Exception e) {
            throw new UserNullException("User not found!", e);
        }
        return null;
    }

    @Override
    public void updateUser(String username, UserData u) throws DataAccessException {
        // maybe get rid of this?
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        userList.remove(getUser(username));
    }

    @Override
    public void clear() throws DataAccessException {
        userList.clear();
    }
}
