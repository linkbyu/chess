package MySqlDAOs;

import dataaccess.MySqlDAOs.DatabaseManager;
import dataaccess.MySqlDAOs.MySqlUserDAO;
import dataaccess.exception.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlUserDAOTest {
    private Connection conn;
    private static MySqlUserDAO userDAO;


    @BeforeAll
    static void configDatabase() throws DataAccessException {
        //DatabaseManager.getConnection(true);
        userDAO = new MySqlUserDAO();

        //DatabaseManager.closeConnection(true);
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        conn = DatabaseManager.getConnection(true);
        userDAO.addUser(new UserData("thomas", "winner", null));
    }

    @AfterEach
    void cleanUp() throws DataAccessException {
        DatabaseManager.closeConnection(conn, false);
        conn = null;
    }

    /*@AfterAll
    static void finish() throws SQLException {
        DatabaseManager.closeConnection(false);
    }*/



    @Test
    void addUserSuccess() throws DataAccessException {
        var username = "gerald1";
        userDAO.addUser(new UserData(username, "random", null));
        Assertions.assertEquals(username, userDAO.getUser(username).username());
    }

    @Test
    void addUserAlreadyTaken() {
        Assertions.assertThrows(SQLException.class, () ->
                userDAO.addUser(new UserData("thomas", "duplicate", "duplicate")) );
    }


}
