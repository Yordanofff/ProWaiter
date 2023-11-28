package FrontEnd;

import BackEnd.Users.User;

// This method allows passing in a method reference with an integer as a parameter
@FunctionalInterface
public interface MenuAction {
    void execute(int option, User user);
}
