package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DbConnection {

    private static String url;
    private static String user;
    private static String password;

    static {
        try {
            Properties props = new Properties();

            InputStream input = DbConnection.class
                    .getClassLoader()
                    .getResourceAsStream("db.properties");

            if (input == null) {
                File externalFile = new File("db.properties");

                if (externalFile.exists()) {
                    input = new FileInputStream(externalFile);
                } else {
                    throw new RuntimeException(
                            "找不到 db.properties，請確認檔案放在 src/main/resources/db.properties，"
                                    + "或放在 jar 檔同一層資料夾。");
                }
            }

            props.load(input);

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

            if (url == null || user == null || password == null) {
                throw new RuntimeException("db.properties 內容不完整，請確認有 db.url、db.user、db.password");
            }

            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (Exception e) {
            throw new RuntimeException("資料庫設定載入失敗", e);
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, user, password);
    }
}