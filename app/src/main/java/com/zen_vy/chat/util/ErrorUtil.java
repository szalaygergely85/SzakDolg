package com.zen_vy.chat.util;

import android.content.Context;
import android.widget.Toast;

public class ErrorUtil {

   public static String getErrorMessage(String errorCode, Context context) {
      int errorID = context
         .getResources()
         .getIdentifier(errorCode, "string", context.getPackageName());
      return context.getString(errorID);
   }

   public static void GetErrorMessageInToast(
      String errorCode,
      Context context
   ) {
      Toast
         .makeText(
            context,
            getErrorMessage(errorCode, context),
            Toast.LENGTH_SHORT
         )
         .show();
   }
}
