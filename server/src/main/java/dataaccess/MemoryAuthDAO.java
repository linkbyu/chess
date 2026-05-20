package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private final Collection<AuthData> authList;

    public MemoryAuthDAO() {
        authList = new ArrayList<>();
    }


    @Override
    public AuthData createAuth(String username){
        String authToken = UUID.randomUUID().toString();
        var auth = new AuthData(authToken, username);

        authList.add(auth);
        return auth;
    }

    @Override
    public AuthData getAuth(String authToken){
        for (AuthData auth : authList){
            if ( authToken.equals(auth.authToken()) ){
                return auth;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken){
        AuthData oldAuth = getAuth(authToken);
        authList.remove(oldAuth);
    }

    @Override
    public void clear(){
        authList.clear();
    }
}
