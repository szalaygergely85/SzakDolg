package com.example.szakdolg.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class IntentUtil {
    public static void startActivity(Context context, Class<?> targetActivity, Bundle additionalExtras) {
        Intent intent = new Intent(context, targetActivity);
        if (additionalExtras != null) {
            intent.putExtras(additionalExtras);
        }
        context.startActivity(intent);
    }
}
