package com.example.szakdolg.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FileUtil {

   public static final String USER_AGENT_FIREFOX =
      "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10 (.NET CLR 3.5.30729)";

   public static void writeHashMapToFile(
      HashMap<String, String> hashMap,
      File file
   ) {
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

   public static Uri getUri(String name, Context c) {
      Uri uri;
      File file = new File(c.getFilesDir() + "/Pictures/" + name);
      if (file.exists()) {
         uri = Uri.fromFile(file);
      } else {
         uri = null;
      }
      return uri;
   }

   public static void saveFileFromUri(
      Uri fileUri,
      File file,
      Runnable runnable
   ) {
      ensureDirectoryExists(file);

      OkHttpClient client = new OkHttpClient();
      Request request = new Request.Builder()
         .url(fileUri.toString())
         .header(
            "User-Agent",
            "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10 (.NET CLR 3.5.30729)"
         )
         .build();

      client
         .newCall(request)
         .enqueue(
            new Callback() {
               @Override
               public void onFailure(
                  @NonNull Call call,
                  @NonNull IOException e
               ) {}

               @Override
               public void onResponse(
                  @NonNull Call call,
                  @NonNull Response response
               ) throws IOException {
                  if (!response.isSuccessful()) {
                     throw new IOException("Unexpected code " + response);
                  }
                  try (
                     InputStream inputStream = response.body().byteStream();
                     FileOutputStream outputStream = new FileOutputStream(file)
                  ) {
                     byte[] buffer = new byte[8 * 1024];
                     int read;
                     while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                     }
                     Log.e("FileApiHelper", "File saved" + file.toString());
                     runnable.run();
                     outputStream.flush();
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
               }
            }
         );
   }

   public static String getFileExtensionFromUri(Uri uri) {
      String path = uri.getPath();
      if (path == null) {
         return null;
      }

      int lastDotIndex = path.lastIndexOf('.');
      if (lastDotIndex == -1) {
         return null;
      }

      return path.substring(lastDotIndex + 1);
   }

   public static void ensureDirectoryExists(File file) {
      File directory = file.getParentFile();
      if (directory != null && !directory.exists()) {
         if (directory.mkdirs()) {
            System.out.println(
               "Directory created successfully: " + directory.getPath()
            );
         } else {
            System.out.println(
               "Failed to create directory: " + directory.getPath()
            );
         }
      }
   }

   public static File writeResponseBodyToDisk(
      ResponseBody body,
      Context c,
      String fileName
   ) throws IOException {
      File futureFile = null;
      try {
         File path = new File(c.getFilesDir() + "/Pictures/");
         if (!path.exists()) {
            path.mkdir();
         }
         futureFile = new File(path + File.separator + fileName);
         InputStream inputStream = null;
         OutputStream outputStream = null;
         try {
            byte[] fileReader = new byte[4096];
            long fileSize = body.contentLength();
            long fileSizeDownloaded = 0;
            inputStream = body.byteStream();
            outputStream = new FileOutputStream(futureFile);

            while (true) {
               int read = inputStream.read(fileReader);

               if (read == -1) {
                  break;
               }

               outputStream.write(fileReader, 0, read);

               fileSizeDownloaded += read;
            }
            outputStream.flush();
         } catch (IOException e) {} finally {
            if (inputStream != null) {
               inputStream.close();
            }

            if (outputStream != null) {
               outputStream.close();
            }
         }
      } catch (IOException e) {}
      return futureFile;
   }
}
