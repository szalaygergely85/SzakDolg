package com.example.szakdolg;


import android.content.Context;
import android.widget.Toast;

public class Error {
    public static String getErrorMessage(String errorCode, Context context) {
        int errorID = context.getResources().getIdentifier(errorCode, "string", context.getPackageName());
        return context.getString(errorID);
    }

    public static void GetErrorMessageInToast(String errorCode, Context context) {
        Toast.makeText(context, getErrorMessage(errorCode, context), Toast.LENGTH_SHORT).show();
    }
}
