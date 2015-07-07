package cs601.webmail.managers;

import java.sql.Connection;

public class FolderModel {

    public static Boolean debug = true;
    public static FolderManager objects;
    private Connection c;

    public FolderModel() {
        this.objects = FolderManager.instance();
    }
}
