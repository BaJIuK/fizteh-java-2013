package ru.fizteh.fivt.students.annasavinova.filemap;

import ru.fizteh.fivt.students.annasavinova.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class FileMap {
    private static RandomAccessFile dataFile;

    private static String getKey(long pointer) {
        try {
            dataFile.seek(pointer);
            int keyLong = dataFile.readInt();
            int valueLong = dataFile.readInt();
            byte[] byteArray = new byte[keyLong];
            dataFile.read(byteArray, 0, keyLong);
            String key = new String(byteArray, "UTF-16");
            dataFile.skipBytes(valueLong);
            return key;
        } catch (IOException e) {
            System.out.println("Can't read key");
            System.exit(1);
        }
        return "PANIC_KEY";
    }

    private static String getValue(long pointer) {
        try {
            dataFile.seek(pointer);
            int keyLong = dataFile.readInt();
            int valueLong = dataFile.readInt();
            byte[] byteArray = new byte[valueLong];
            dataFile.skipBytes(keyLong);
            dataFile.read(byteArray, 0, valueLong);
            String value = new String(byteArray, "UTF-16");
            return value;
        } catch (IOException e) {
            System.out.println("Can't read value");
            System.exit(1);
        }
        return "PANIC_VALUE";
    }

    private static long findKey(String key) {
        try {
            dataFile.seek(0);
            long currPointer = dataFile.getFilePointer();
            while (currPointer != dataFile.length()) {
                if (key.equals(getKey(currPointer))) {
                    return currPointer;
                }
                currPointer = dataFile.getFilePointer();
            }
        } catch (IOException e) {
            System.out.println("Can't find key");
            System.exit(1);
        }
        return -1;
    }

    public static void doPut(String key, String value) {
        try {
            long pointToKey = findKey(key);
            if (dataFile.length() == 0 || pointToKey == -1) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(getValue(pointToKey));
                delete(key);
            }
            dataFile.seek(dataFile.length());
            dataFile.writeInt(2 * key.length());
            dataFile.writeInt(2 * value.length());
            dataFile.writeChars(key);
            dataFile.writeChars(value);
        } catch (IOException e) {
            System.out.println("Can't put");
            System.exit(1);
        }
    }

    public static void doGet(String key) {
        long findRes = findKey(key);
        try {
            if (findRes == -1 || dataFile.length() == 0) {
                System.out.println("not found");
            } else {
                String value = getValue(findRes);
                System.out.println("found");
                System.out.println(value);
            }
        } catch (IOException e) {
            System.out.println("Can't get");
            System.exit(1);
        }
    }

    private static void copy(RandomAccessFile sourse, RandomAccessFile dest, long off, long length) {
        int tmp = (int) length;
        if (tmp < 0) {
            System.out.println(tmp);
        }
        byte[] arr = new byte[tmp];
        try {
            sourse.seek(off);
            sourse.read(arr, 0, tmp);
            dest.seek(dest.length());
            dest.write(arr);
        } catch (IOException e) {
            System.out.println("Can't rewrite file");
            System.exit(1);
        }

    }

    private static void delete(String key) {
        try {
            long keyPointer = findKey(key);
            File tmp = File.createTempFile("DataBase", key);
            RandomAccessFile tmpFile = new RandomAccessFile(tmp, "rw");
            copy(dataFile, tmpFile, 0, keyPointer);
            dataFile.seek(keyPointer);
            int keyLong = dataFile.readInt();
            int valueLong = dataFile.readInt();
            long recordEnd = dataFile.getFilePointer() + keyLong + valueLong;
            copy(dataFile, tmpFile, recordEnd, dataFile.length() - recordEnd);
            dataFile.setLength(0);
            copy(tmpFile, dataFile, 0, tmpFile.length());
            dataFile.setLength(tmpFile.length());
            tmpFile.close();
        } catch (IOException e) {
            System.out.println("Can't remove");
            System.exit(1);
        }
    }

    public static void doRemove(String key) {
        long keyPointer = findKey(key);
        if (keyPointer == -1) {
            Shell.printError("not found");
        } else {
            delete(key);
            System.out.println("removed");
        }
    }

    private static void execCommand(String[] args) {
        try {
            // dataFile = new
            // RandomAccessFile(System.getProperty("fizteh.db.dir"), "rw");
            dataFile = new RandomAccessFile("dbfile", "rw");
            switch (args[0]) {
            case "put":
                if (Shell.checkArgs(3, args)) {
                    doPut(args[1], args[2]);
                }
                break;
            case "get":
                if (Shell.checkArgs(2, args)) {
                    doGet(args[1]);
                }
                break;
            case "remove":
                if (Shell.checkArgs(2, args)) {
                    doRemove(args[1]);
                }
                break;
            default:
                System.out.println("Unknown command");
            }
            dataFile.close();
        } catch (IOException e) {
            System.out.println("Can't open or close file");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args.length != 0) {
            Shell.isPacket = true;
            StringBuffer argStr = new StringBuffer(args[0]);
            for (int i = 1; i < args.length; ++i) {
                argStr.append(" ");
                argStr.append(args[i]);
            }
            Scanner mainScanner = new Scanner(argStr.toString());
            mainScanner.useDelimiter("[ ]*;[ ]*");
            while (mainScanner.hasNext()) {
                String str = mainScanner.next();
                execCommand(Shell.getArgsFromString(str));
            }
            mainScanner.close();
        } else {
            System.out.print("$ ");
            Scanner mainScanner = new Scanner(System.in);
            mainScanner.useDelimiter(System.lineSeparator());
            while (mainScanner.hasNext()) {
                String str = new String();
                str = mainScanner.next();
                if (str.equals("exit")) {
                    mainScanner.close();
                    return;
                }
                execCommand(Shell.getArgsFromString(str));
                System.out.print("$ ");
            }
            mainScanner.close();
            return;
        }
    }
}
