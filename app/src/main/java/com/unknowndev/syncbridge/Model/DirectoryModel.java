package com.unknowndev.syncbridge.Model;

public class DirectoryModel {
    private String Name;
    private String Type;
    private String Path;
    private String Size;
    private String SubFolderCount;
    private boolean isFolder;

    public DirectoryModel() {
    }

    public DirectoryModel(String name, String type, String path, String size, String subFolderCount, boolean isFolder) {
        Name = name;
        Type = type;
        Path = path;
        Size = size;
        SubFolderCount = subFolderCount;
        this.isFolder = isFolder;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getSubFolderCount() {
        return SubFolderCount;
    }

    public void setSubFolderCount(String subFolderCount) {
        SubFolderCount = subFolderCount;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }
}
