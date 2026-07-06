package util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DbConnection {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = DbConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("找不到 src/main/resources/db.properties");
            }
            PROPERTIES.load(input);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new RuntimeException("資料庫設定載入失敗", e);
        }
    }

    public static Connection getConnection() throws Exception {
        String url = PROPERTIES.getProperty("db.url");
        String user = PROPERTIES.getProperty("db.user");
        String password = PROPERTIES.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }
}
