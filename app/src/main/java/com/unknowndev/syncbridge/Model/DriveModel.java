package com.unknowndev.syncbridge.Model;

import java.io.Serializable;

public class DriveModel implements Serializable {
    private String DriveName;
    private String DrivePath;
    private Long AvailableSpace;
    private Long TotalSpace;
    public DriveModel() {
    }
    public DriveModel(String driveName, String drivePath, Long availableSpace, Long totalSpace) {
        DriveName = driveName;
        DrivePath = drivePath;
        AvailableSpace = availableSpace;
        TotalSpace = totalSpace;
    }

    public String getDriveName() {
        return DriveName;
    }

    public void setDriveName(String driveName) {
        DriveName = driveName;
    }

    public String getDrivePath() {
        return DrivePath;
    }

    public void setDrivePath(String drivePath) {
        DrivePath = drivePath;
    }

    public Long getAvailableSpace() {
        return AvailableSpace;
    }

    public void setAvailableSpace(Long availableSpace) {
        AvailableSpace = availableSpace;
    }

    public Long getTotalSpace() {
        return TotalSpace;
    }

    public void setTotalSpace(Long totalSpace) {
        TotalSpace = totalSpace;
    }
}
