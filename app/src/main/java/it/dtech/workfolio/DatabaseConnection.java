package it.dtech.workfolio;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {
    public static Properties props = new Properties();
    public static Connection connect() {
        new DatabaseTask().execute();
        String connectionUrl = "jdbc:jtds:sqlserver://dtech.database.windows.net:1433/dtech;sslProtocol=TLSv1.2;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

        try {
            return DriverManager.getConnection(connectionUrl, props);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void close(Connection connection, Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            // Handle the exception or log it
            e.printStackTrace();
        }
    }
    @SuppressLint("StaticFieldLeak")
    static
    class DatabaseTask extends AsyncTask<Void, Void, Void> {

        SharedPreferences sp;
        @Override
        protected Void doInBackground(Void... voids) {
            props.setProperty("user", "srvadmin");
            props.setProperty("password", "DTech@38324");
            props.setProperty("ssl", "true"); // Enable SSL/TLS
            return null;
        }
    }
}

