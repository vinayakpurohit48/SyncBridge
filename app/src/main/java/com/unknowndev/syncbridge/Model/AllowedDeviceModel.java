package com.unknowndev.syncbridge.Model;

public class AllowedDeviceModel {
    private String SessionID;
    private String UserID;

    public AllowedDeviceModel(String sessionID, String userID) {
        SessionID = sessionID;
        UserID = userID;
    }

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
