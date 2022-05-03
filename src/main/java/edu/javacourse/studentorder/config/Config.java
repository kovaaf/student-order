package edu.javacourse.studentorder.config;
/*
 *   Created by Kovalyov Anton 09.04.2022
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static final String DB_URL = "db.url";
    public static final String DB_LOGIN = "db.login";
    public static final String DB_PASSWORD = "db.password";
    public static final String DB_LIMIT = "db.limit";
    public static final String CR_URL = "cr.url";

    private static Properties properties = new Properties();

    public synchronized static String getProperty(String name) {
        if (properties.isEmpty()) {
            try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
                properties.load(inputStream);
            } catch (IOException ex) {
                ex.printStackTrace();
                // Это имеет смысл т.к. мы не хотим запускать приложение, если настройки не загрузились
                throw new RuntimeException(ex);
            }
        }
        return properties.getProperty(name);
    }
}
