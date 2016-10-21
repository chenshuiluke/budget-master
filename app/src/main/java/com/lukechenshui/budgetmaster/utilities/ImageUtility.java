package com.lukechenshui.budgetmaster.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by luke on 10/17/16.
 */

public class ImageUtility {
    public static byte[] getBytes(Context context, Uri uri) {
        try{
            if (uri != null) {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];

                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }
                return byteBuffer.toByteArray();
            } else {
                return null;
            }

        }
        catch (Exception exc){
            Log.d("Image", "", exc);
            return null;
        }

    }
}
