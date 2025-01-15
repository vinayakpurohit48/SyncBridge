package com.unknowndev.syncbridge.Model;

public class FileData {
    private String Name;
    private String Size;
    private String SubFolderCount;
    private boolean isFolder;
    private String Path;

    public FileData(String name, String size, String subFolderInfo, boolean isFolder, String currentPath) {
        this.Name = name;
        this.Size = size;
        this.SubFolderCount = subFolderInfo;
        this.isFolder = isFolder;
        this.Path = currentPath;
    }

    public String getName() {
        return Name;
    }

    public String getSize() {
        return Size;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        this.Path = path;
    }

    public void setSize(String size) {
        this.Size = size;
    }

    public void setSubFolderCount(String subFolderCount) {
        this.SubFolderCount = subFolderCount;
    }

    public String getSubFolderCount() {
        return SubFolderCount;
    }

    public boolean isFolder() {
        return isFolder;
    }
}
