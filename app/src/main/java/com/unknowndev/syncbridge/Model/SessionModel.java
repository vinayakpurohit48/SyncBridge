package com.unknowndev.syncbridge.Model;

import java.io.Serializable;

public class SessionModel implements Serializable {
    private String SessionID;
    private String UserID;
    private String DeviceType;
    private String AccessUrl;

    public SessionModel() {
    }

    public SessionModel(String sessionID, String userID, String deviceType, String accessUrl) {
        SessionID = sessionID;
        UserID = userID;
        DeviceType = deviceType;
        AccessUrl = accessUrl;
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

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public String getAccessUrl() {
        return AccessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        AccessUrl = accessUrl;
    }
}
