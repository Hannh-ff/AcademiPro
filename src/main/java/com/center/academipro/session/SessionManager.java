package com.center.academipro.session;

public class SessionManager {
    private static SessionManager instance;
    private String username;
    private int userId;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUser(String username, int userId) {
        this.username = username;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
    }

    public void clearSession() {
        username = null;
        userId = -1;
    }
}
