package lk.ijse.dep11.app.controller;

import lk.ijse.dep11.app.tm.User;

public class UserDetails {
    private static User loggedUser;

    public static User getLoggedUser() {
        return loggedUser;
    }

    public static void setLoggedUser(User loggedUser) {
        UserDetails.loggedUser = loggedUser;
    }

}
