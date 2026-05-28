package dataaccess;

import dataaccess.mysqldao.MySqlAuthDAO;
import dataaccess.exception.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.*;

public class MySqlAuthDAOTest {
    private static MySqlAuthDAO authDAO;
    private AuthData exampleAuth;

    @BeforeAll
    static void configDatabase() throws DataAccessException {
        authDAO = new MySqlAuthDAO();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        exampleAuth = authDAO.createAuth("leon");
    }

    @AfterEach
    void cleanUp() throws DataAccessException {
        authDAO.clear();
    }



    @Test
    void createAuthSameUser() throws DataAccessException {
        var extraAuth = authDAO.createAuth("leon");

        Assertions.assertNotEquals(extraAuth, exampleAuth);
    }

    @Test
    void createAuthSuccess() throws DataAccessException {
        AuthData a = authDAO.createAuth("timmy");

        AuthData a2 = authDAO.getAuth(a.authToken());
        Assertions.assertEquals(a, a2);
    }

    @Test
    void getAuthSuccess() throws DataAccessException {
        AuthData a2 = authDAO.getAuth(exampleAuth.authToken());
        Assertions.assertEquals(exampleAuth, a2);
    }

    @Test
    void getAuthNull() throws DataAccessException {
        Assertions.assertNull(authDAO.getAuth("abcdefghijklmnop"));
    }

    @Test
    void deleteAuthNull() throws DataAccessException {
        authDAO.deleteAuth("abcdefghijklmnop");

        AuthData a2 = authDAO.getAuth(exampleAuth.authToken()); // example AuthData hasn't changed from delete
        Assertions.assertEquals(exampleAuth, a2);
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {
        authDAO.deleteAuth(exampleAuth.authToken());

        Assertions.assertNull(authDAO.getAuth(exampleAuth.authToken()) );
    }

    @Test
    void clearAuth() throws DataAccessException {
        authDAO.clear();
        Assertions.assertNull(authDAO.getAuth(exampleAuth.authToken()) );
    }

}
