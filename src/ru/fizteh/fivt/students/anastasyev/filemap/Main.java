package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Launcher;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        //System.getProperties().setProperty("fizteh.db.dir", "C:\\Users\\qBic
        // \\Documents\\GitHub\\fizteh-java-2013\\src\\ru\\fizteh\\fivt\\students\\anastasyev\\filemap\\db.dir");
        if (System.getProperty("fizteh.db.dir") == null) {
            System.err.println("Set home data base's directory");
            System.err.println("Use: -Dfizteh.db.dir=<directory>");
            System.exit(1);
        }
        FileMapTableProvider fileMapTableProvider = null;
        try {
            fileMapTableProvider = new FileMapTableProvider(System.getProperty("fizteh.db.dir"));
            Launcher launcher = new Launcher(fileMapTableProvider, args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
