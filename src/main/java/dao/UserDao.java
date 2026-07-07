package dao;

import model.User;

public interface UserDao {
    User login(String username, String password);
}
