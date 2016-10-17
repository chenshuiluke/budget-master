package com.lukechenshui.budgetmaster.utilities;

import android.content.ContentResolver;
import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.net.Uri;
import android.util.Log;

/**
 * Created by luke on 10/17/16.
 */

public class ImageUtility {
    public static byte[] getBytes(Context context, Uri uri) {
        try{
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        }
        catch (Exception exc){
            Log.d("Image", "", exc);
            return null;
        }

    }
}
