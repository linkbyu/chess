package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryAuthDAO implements AuthDAO{
    private final Collection<AuthData> authList;

    public MemoryAuthDAO() {
        authList = new ArrayList<>();
    }


    @Override
    public void addAuth(AuthData auth) throws DataAccessException {
        authList.add(auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData auth : authList){
            if ( authToken.equals(auth.authToken()) ){
                return auth;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        AuthData oldAuth = getAuth(authToken);
        authList.remove(oldAuth);
    }

    @Override
    public void clear() throws DataAccessException {
        authList.clear();
    }
}
