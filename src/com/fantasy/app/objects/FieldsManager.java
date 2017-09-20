package com.fantasy.app.objects;

import com.fantasy.app.notification.Notification;

import java.sql.*;

public class FieldsManager {
    private Notification notification = new Notification();

    private DatabaseManager databaseManager;
    private XmlManager xmlManager;

    private final String pathToFirstXml  = "\\xml\\1.xml";
    private final String pathToSecondXml = "\\xml\\2.xml";
    private final String pathToXslXml    = "\\xml\\xsl.xml";
    private final String defaultTableName  = "TEST";
    private final String defaultColumnName = "FIELD";

    private String currentDir;
    private String tableName;
    private String colName;

    private int n;

    public FieldsManager() {
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public XmlManager getXmlManager() {
        return xmlManager;
    }
    public void setXmlManager(XmlManager xmlManager) {
        this.xmlManager = xmlManager;
    }

    public int getN() {
        return n;
    }
    public void setN(int n) {
        this.n = n;
    }

    public String createUrl(String dbInfo, String pathToDb){
        if(currentDir==null){
            currentDir = System.getProperty("user.dir");
        }
        return createUrl(dbInfo,currentDir,pathToDb);
    }
    public String createUrl(String dbInfo, String currentDir, String pathToDb){
        this.currentDir = currentDir;
        return dbInfo+currentDir+pathToDb;
    }

    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColName() {
        return colName;
    }
    public void setColName(String colName) {
        this.colName = colName;
    }

    private boolean insertFields(){
        notification = databaseManager.getNotification();

        if(databaseManager.isAnyField(tableName, colName)){
            if(notification.hasErrorsThanPrint()) return false;

            databaseManager.clearFields(tableName);
            if(notification.hasErrorsThanPrint()) return false;
        }

        databaseManager.insertNFieldsTo(n, tableName, colName);
        if(notification.hasErrorsThanPrint()) return false;

        return true;
    }

    private void connection(){
        databaseManager.connection();
        notification = databaseManager.getNotification();
    }

    private boolean createXmlFromFields(){
        ResultSet resultSet = databaseManager.getResultSetOfSelection(tableName, colName);
        if(databaseManager.getNotification().hasErrorsThanPrint()) return false;

        xmlManager.createXml(resultSet);
        if(xmlManager.getNotification().hasErrorsThanPrint()) return false;

        return true;
    }

    private void correctionTableValues(){
        if(tableName==null){
            tableName = defaultTableName;
        }
        if(colName==null){
            colName = defaultColumnName;
        }
    }

    private boolean checkValues(){
        databaseManager.checkForNulls();
        notification = databaseManager.getNotification();
        if(notification.hasErrorsThanPrint()) return true;

        if(currentDir==null){
            notification.addError("currentDir can't be null", new NullPointerException());
        }
        if(n<1){
            notification.addError("N can't be less than 1", new NullPointerException());
        }
        if(notification.hasErrorsThanPrint()) return true;

        return false;
    }

    public void startLogic(){
        boolean allRight = !checkValues();
        if(!allRight) return;

        xmlManager = new XmlManager(currentDir, pathToFirstXml, pathToSecondXml, pathToXslXml);

        connection();
        if(notification.hasErrorsThanPrint()) return;

        correctionTableValues();

        allRight = insertFields();
        if(!allRight) return;

        allRight = createXmlFromFields();
        if(!allRight) return;

        xmlManager.transformXml();
        if(xmlManager.getNotification().hasErrorsThanPrint()) return;

        long arithmeticMean = xmlManager.getArithmeticMeanFromFields();
        if(xmlManager.getNotification().hasErrorsThanPrint()) return;

        System.out.println("Среднее арифметическое: " + arithmeticMean);
    }
}
