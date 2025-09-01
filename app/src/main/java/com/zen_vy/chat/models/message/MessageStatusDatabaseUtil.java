package com.zen_vy.chat.models.message;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zen_vy.chat.database.DatabaseHelper;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.message.entity.MessageStatus;
import com.zen_vy.chat.models.message.entity.MessageStatusType;
import com.zen_vy.chat.models.user.entity.User;

public class MessageStatusDatabaseUtil {
    private final DatabaseHelper dbHelper;

    public MessageStatusDatabaseUtil(Context context, User user) {
        dbHelper = DatabaseHelper.getInstance(context, user.getUuid());
    }

    public void insertMessageStatus(MessageStatus messageStatus) {

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("uuid", messageStatus.getUuid());
        MessageStatusType messageStatusType = messageStatus.getMessageStatusType();
            values.put("messageStatusType", messageStatusType.name());

            db.insertWithOnConflict(
                    dbHelper.TABLE_MESSAGE_STATUS,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );

    }
}
