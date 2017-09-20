package com.fantasy.app.objects;

import com.fantasy.app.notification.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Djelu on 13.09.2017.
 */
public class DatabaseManager {
    private Notification notification = new Notification();

    private Connection connection;
    private String driver;
    private String connectionUrl;
    private String username;
    private String password;

    public DatabaseManager(String driver, String connectionUrl) {
        this(driver,connectionUrl,"","");
    }
    public DatabaseManager(String driver, String connectionUrl, String username, String password) {
        this.driver = driver;
        this.connectionUrl = connectionUrl;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getDriver() {
        return driver;
    }
    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }
    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public Notification getNotification() {
        return notification;
    }

    public boolean isAnyField(String tableName, String colName){
        boolean result = false;

        try {
            Statement statement = connection.createStatement();

            String query = "select " + colName + " from " + tableName;
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                result = true;
            }
        }catch (SQLException e){
            String message = String.format("Error with find out if is fields in table(%s) in columns(%s)",tableName,colName);
            notification.addError(message, e);
        }

        return result;
    }

    public void clearFields(String tableName){
        String query;
        try {
            Statement statement = connection.createStatement();
            query = "delete from " + tableName;
            statement.executeUpdate(query);
        }catch (SQLException e){
            String message = String.format("Can not clear table(%s)",tableName);
            notification.addError(message, e);
        }
    }

    public void insertNFieldsTo(int n, String tableName, String colName) {
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement;

            preparedStatement = connection.prepareStatement("insert into " + tableName + "(" + colName + ") values (?)");

            for(int i=1; i<=n; i++){
                preparedStatement.setInt(1, i);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            String message = String.format("Error with insert N(%d) filds in table(%s) column(%s)",n,tableName,colName);
            notification.addError(message, e);
        }
    }

    public ResultSet getResultSetOfSelection(String tableName, String colName){
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select " + colName + " from " + tableName);
        } catch (SQLException e) {
            notification.addError(String.format("Can not select fields from table(%s), column(%s)",tableName,colName), e);;
        }
        return resultSet;
    }

    public void connection(){
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            notification.addError(String.format("Can not find driver(%s)",driver), e);
        }
        try {
            connection = DriverManager.getConnection(connectionUrl, username, password);
        } catch (SQLException e) {
            String message = String.format("Can not connection to url(%s) with username(%s), password(%s)",connectionUrl,username,password);
            notification.addError(message, e);
        }
    }

    public void checkForNulls(){
        if(connectionUrl==null){
            notification.addError("connectionUrl can't be null", null);
        }
        if(driver==null){
            notification.addError("driver can't be null", null);
        }
    }
}
