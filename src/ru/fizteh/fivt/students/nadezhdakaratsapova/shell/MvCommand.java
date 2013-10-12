package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MvCommand implements Command {
    public String getName() {
        return "mv";
    }

    public void execute(CurrentDirectory currentDirectory, String[] args) throws IOException {
        File source = new File(args[1]).getCanonicalFile();
        if (!source.isAbsolute()) {
            source = new File(currentDirectory.getCurDir(), args[1]);
        }
        File destination = new File(args[2]).getCanonicalFile();
        if (!destination.isAbsolute()) {
            destination = new File(currentDirectory.getCurDir(), args[2]);
        }
        if (!source.exists()) {
            throw new IOException("mv: " + args[1] + " was not found");
        }
        if (source.equals(destination)) {
            throw new IOException("mv: " + args[1] + " and " + args[2] + " are same files");
        }

        if (source.isFile()) {
            if (destination.exists()) {
                if (destination.isDirectory()) {
                    File target = new File(destination, source.getName());
                    if (target.exists()) {
                        throw new IOException("mv: " + source.getName() + " already exists in " + destination.getName());
                    }
                    Files.move(source.toPath(), target.toPath());
                } else {
                    throw new IOException("mv: not managed to copy File " + source.getName() + " to File " + destination.getName());
                }
            } else {
                File target = new File(source, destination.getName());
                if (target.equals(destination)) {
                    source.renameTo(destination);
                } else {
                    throw new IOException("mv: not managed to copy File " + source.getName() + " to File " + destination.getName());
                }
            }
        } else {
            if (destination.isDirectory()) {
                File target = new File(destination, source.getName());
                if (target.exists()) {
                    throw new IOException("mv: " + source.getName() + " already exists in " + destination.getName());
                }
                moveRec(source, destination);
            } else {
                throw new IOException("mv: not managed to copy Directory " + source.getName() + " to File " + destination.getName());
            }
        }

    }

    private void moveRec(File src, File dest) throws IOException {
        File target = new File(dest, src.getName());
        if (src.isDirectory()) {
            target.mkdir();
            File[] fileList = src.listFiles();
            if (fileList.length > 0) {
                for (File file : fileList) {
                    moveRec(file, target);
                }
            }
            src.delete();
        } else {
            Files.move(src.toPath(), target.toPath());
        }

    }

    public int getArgsCount() {
        return 2;
    }
}
