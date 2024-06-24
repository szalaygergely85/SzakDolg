package com.example.szakdolg.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class FileUtil {

        public static void writeHashMapToFile(HashMap<String, String> hashMap, File file) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                // Write HashMap to ObjectOutputStream
                oos.writeObject(hashMap);

                // Close streams
                oos.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public static void writeStringToFile(String data, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Write HashMap to ObjectOutputStream
            oos.writeObject(data);

            // Close streams
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static HashMap<String, String> readHashMapFromFile(File file) {
        HashMap<String, String> hashMap = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Read HashMap from ObjectInputStream
            hashMap = (HashMap<String, String>) ois.readObject();

            // Close streams
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public static String readStringFromFile(File file) {
        String data = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            data = (String) ois.readObject();

            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

}
