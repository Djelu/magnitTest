package com.fantasy.app.start;

import com.fantasy.app.objects.DatabaseManager;
import com.fantasy.app.objects.FieldsManager;

import javax.xml.transform.TransformerException;

/**
 * Created by Djelu on 12.09.2017.
 */
public class Main {

    public static void main(String[] args) throws TransformerException {
        FieldsManager fieldsManager = new FieldsManager();

        String currentDir = System.getProperty("user.dir");
        String url = fieldsManager.createUrl("jdbc:sqlite:", currentDir, "\\db\\some.db");
        String driver = "org.sqlite.JDBC";
        DatabaseManager databaseManager = new DatabaseManager(driver,url);

        fieldsManager.setDatabaseManager(databaseManager);
        fieldsManager.setN(1000000);

        long t1 = System.currentTimeMillis();

        fieldsManager.startLogic();

        long t2 = System.currentTimeMillis();

        System.out.println("Время работы: " + (double)(t2-t1)/1000 + " секунд.");
    }
}
