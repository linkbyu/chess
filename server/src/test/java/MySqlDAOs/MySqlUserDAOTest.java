package MySqlDAOs;

import dataaccess.mysqldao.MySqlUserDAO;
import dataaccess.exception.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;


public class MySqlUserDAOTest {

    private static MySqlUserDAO userDAO;


    @BeforeAll
    static void configDatabase() throws DataAccessException {
        userDAO = new MySqlUserDAO();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO.addUser(new UserData("thomas", "winner", null));
    }

    @AfterEach
    void cleanUp() throws DataAccessException {
        userDAO.clear();
    }


    @Test
    void addUserSuccess() throws DataAccessException {
        var username = "gerald1";
        userDAO.addUser(new UserData(username, "random", null));
        Assertions.assertEquals(username, userDAO.getUser(username).username());
    }

    @Test
    void addUserAlreadyTaken() {
        Assertions.assertThrows(DataAccessException.class, () ->
                userDAO.addUser(new UserData("thomas", "duplicate", "duplicate")) );
    }

    @Test
    void getUserNull() throws DataAccessException {
        Assertions.assertNull(userDAO.getUser("nonExistent") );
    }

    @Test
    void getUserSuccess() throws DataAccessException {
        Assertions.assertEquals("thomas",
                userDAO.getUser("thomas").username());
    }

    @Test
    void clearUsersSuccess() throws DataAccessException {
        userDAO.clear();
        Assertions.assertNull(userDAO.getUser("thomas") );
    }


}
