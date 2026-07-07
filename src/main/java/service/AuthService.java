package service;

import exception.AppException;
import model.User;

public interface AuthService {
    User login(String username, String password) throws AppException;
}
