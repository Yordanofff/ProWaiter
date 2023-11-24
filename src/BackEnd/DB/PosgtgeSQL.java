package BackEnd.DB;

import BackEnd.Users.User;
import org.postgresql.ds.PGSimpleDataSource;

import java.util.List;

public class PosgtgeSQL {
    private DataAccessObject dao;
    public PosgtgeSQL() {
        init();
    }

    private void init() {
        // Configure the database connection.
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setApplicationName("ProWaiter");

        // todo - config file/sys env
//        ds.setUrl(System.getenv("JDBC_DATABASE_URL"));
        String user = "ivo";
        String pass = "secret";
        String url = "jdbc:postgresql://test1rz-11759.8nj.cockroachlabs.cloud:26257/Waiter?sslmode=require&password=" + pass + "&user=" + user;
        ds.setUrl(url);

        // Create DAO.
        this.dao = new DataAccessObject(ds);

        // Create the accounts table if it doesn't exist
        dao.createUserTableIfNotExist();

        // todo - create Menu + Orders
    }

    public List<User> getUsers(int limit) {
        return dao.getUsers(limit);
    }

    public void addUser(User user) {
        dao.addUser(user);
    }

}