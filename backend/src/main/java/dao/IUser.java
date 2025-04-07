package dao;

import Model.User;
import java.util.List;

public interface IUser {
    User findByEmail(String email);
    boolean registerUser(String name, String email, String password);
    boolean authenticateUser(String email, String password);
    boolean deleteUser(String email);
    boolean updateUser(User updatedUser);
}
