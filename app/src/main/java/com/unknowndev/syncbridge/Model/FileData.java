package com.unknowndev.syncbridge.Model;

public class FileData {
    private String name;
    private String size;
    private String subFolderInfo; // e.g., "4 items"
    private boolean isFolder;
    private String currentPath;

    public FileData(String name, String size, String subFolderInfo, boolean isFolder, String currentPath) {
        this.name = name;
        this.size = size;
        this.subFolderInfo = subFolderInfo;
        this.isFolder = isFolder;
        this.currentPath = currentPath;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setSubFolderInfo(String subFolderInfo) {
        this.subFolderInfo = subFolderInfo;
    }

    public String getSubFolderInfo() {
        return subFolderInfo;
    }

    public boolean isFolder() {
        return isFolder;
    }
}
