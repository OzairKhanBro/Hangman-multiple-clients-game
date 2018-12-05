/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multyclientgamme;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.xdevapi.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uzair
 */
public class DBmanager {

    String url;
    String username;
    String password;
    final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";

    DBmanager(Credentials credentials) {
        url = credentials.getDbConnection();
        username = credentials.getUsername();
        password = credentials.getPassword();
    }

    void testConnection() {
        System.out.println("Trying to connect to database...");
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected!");
        } catch (SQLException e) {
            System.out.println("Unable to connect to database "
                    + url + " with username " + username + " and password " + password);
            throw new IllegalStateException("Cannot connect the database!", e);
        }

    }

    private Connection connection;
    // init properties object
    private Properties properties;

    // create properties
    private Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            properties.setProperty("user", username);
            properties.setProperty("password", password);
        }
        return properties;
    }

    // connect database
    public Connection connect() {
        if (connection == null) {
            try {
                //Class.forName(DATABASE_DRIVER);
                connection = DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                System.out.println("Unable to connect to database.");
                //e.printStackTrace();
            }
        }
        return connection;
    }

    // disconnect database
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                //e.printStackTrace();
            }
        }
    }

    public Person findPerson(String attribute, String value) {
        ResultSet resultSet = null;
        Person p = new Person();
        try {
            PreparedStatement preparedStatement = connect()
                    .prepareStatement("select * from profile where " + attribute + "=?");
            if (attribute == "id") {
                preparedStatement.setInt(1, Integer.parseInt(value));
            } else {
                preparedStatement.setString(1, value);
            }
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                p.setId(resultSet.getString("id"));
                p.setName(resultSet.getString("username"));
                p.setPassword(resultSet.getString("password"));
                p.setWin(resultSet.getInt("win"));
                p.setLoss(resultSet.getInt("loss"));
            }
            disconnect();
        } catch (SQLException ex) {
            System.out.println("unable to read record.");
            //ex.printStackTrace();
            disconnect();
//Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;
    }

    public Person findPersonwithPassword(String name, String password) {
        ResultSet resultSet = null;
        Person p = new Person();
        boolean tem = false;
        try {
            PreparedStatement preparedStatement = connect()
                    .prepareStatement("select * from profile where username=? ");
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                tem = true;
                if (resultSet.getString("password").equals(password)) {
                    p.setId(resultSet.getString("id"));
                    p.setName(resultSet.getString("username"));
                    p.setPassword(resultSet.getString("password"));
                    p.setWin(resultSet.getInt("win"));
                    p.setLoss(resultSet.getInt("loss"));
                }
            }
            disconnect();
            if (tem == false) {
                return null;
            }
        } catch (SQLException ex) {
            System.out.println("unabel to read record.");
//ex.printStackTrace();
            disconnect();
//Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;
    }

    public boolean InsertPerson(Person p) {
        boolean result = false;

        try {
            PreparedStatement preparedStatement = connect()
                    .prepareStatement("insert into profile (`username`, `password`, `win`, `loss`) values ( ?, ?, ?, ? )");
            preparedStatement.setString(1, p.getName());
            preparedStatement.setString(2, p.getPassword());
            preparedStatement.setInt(3, p.getWin());
            preparedStatement.setInt(4, p.getLoss());
            result = preparedStatement.execute();

            disconnect();
        } catch (SQLException ex) {
            System.out.println("unable to insert record.");
//ex.printStackTrace();
            disconnect();
//Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public boolean UpdatePerson(Person p) {
        boolean result = false;
        try {
            PreparedStatement preparedStatement = connect()
                    .prepareStatement("update profile set username=?, password=?,win=?,loss=? where id=?");
            preparedStatement.setString(1, p.getName());
            preparedStatement.setString(2, p.getPassword());
            preparedStatement.setInt(3, p.getWin());
            preparedStatement.setInt(4, p.getLoss());
            preparedStatement.setString(5, p.getId());
            result = preparedStatement.execute();

            disconnect();
        } catch (SQLException ex) {
            //ex.printStackTrace();
            System.out.println("Unable to update Record.");
            disconnect();
//Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
