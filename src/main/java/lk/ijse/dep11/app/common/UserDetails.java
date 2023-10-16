package lk.ijse.dep11.app.common;

import lk.ijse.dep11.app.tm.User;

public class UserDetails {
    private static User loggedUser;
    private static User permittedUser;

    public static User getLoggedUser() {
        return loggedUser;
    }

    public static void setLoggedUser(User loggedUser) {
        UserDetails.loggedUser = loggedUser;
    }

    public static User getPermittedUser() {
        return permittedUser;
    }

    public static void setPermittedUser(User permittedUser) {
        UserDetails.permittedUser = permittedUser;
    }
}
