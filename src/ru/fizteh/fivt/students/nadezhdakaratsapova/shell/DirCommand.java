package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class DirCommand implements Command {
    public String getName() {
        return "dir";
    }

    public void execute(CurrentDirectory currentDirectory, String[] args) {
        if (currentDirectory.getCurDir().isDirectory()) {
            System.out.println(currentDirectory.getCurDir());
            File[] fileList = currentDirectory.getCurDir().getAbsoluteFile().listFiles();
            Arrays.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            if (fileList.length > 0) {
                int i = 0;
                while (fileList[i].getName().charAt(0) == ('.')) {
                    ++i;
                }
                int length = fileList.length;

                for (int j = i; j < length; ++j) {
                    System.out.println(fileList[j].getName());
                }
            }

        }
    }

    public int getArgsCount() {
        return 0;
    }
}
