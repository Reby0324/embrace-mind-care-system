package service.impl;

import dao.UserDao;
import dao.impl.UserDaoImpl;
import exception.AppException;
import model.User;
import service.AuthService;

public class AuthServiceImpl implements AuthService {
    private final UserDao userDao = new UserDaoImpl();

    @Override
    public User login(String username, String password) throws AppException {
        if (isBlank(username) || isBlank(password)) {
            throw new AppException("請輸入帳號與密碼");
        }

        User user = userDao.login(username.trim(), password.trim());
        if (user == null) {
            throw new AppException("帳號或密碼錯誤，或帳號已停用");
        }
        return user;
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
