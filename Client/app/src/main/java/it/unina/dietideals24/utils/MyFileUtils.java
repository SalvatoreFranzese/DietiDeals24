package it.unina.dietideals24.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import id.zelory.compressor.Compressor;

public class MyFileUtils {
    private MyFileUtils() {
    }

    public static File uriToFile(Uri uri, Context context) {
        InputStream inputStream = null;
        File tempFile = null;

        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            tempFile = File.createTempFile("temp", ".jpeg");

            if (inputStream != null) {
                try (FileOutputStream fileOut = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOut.write(buffer, 0, bytesRead);
                    }
                }
            }

            tempFile.deleteOnExit();
            if (inputStream != null) {
                inputStream.close();
            }

            return tempFile;
        } catch (IOException e) {
            if (tempFile != null) {
                tempFile.delete();
            }
            return null;
        }
    }

    public static File compressImage(File imageToBeUploaded, Context applicationContext) {
        try {
            return new Compressor(applicationContext)
                    .setMaxHeight(800)
                    .setMaxWidth(800)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToFile(imageToBeUploaded);

        } catch (IOException e) {
            Log.e("COMPRESSION_ERROR", "image compression failed for: " + imageToBeUploaded);
            return imageToBeUploaded;
        }
    }
}
