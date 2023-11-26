package uk.ac.wlv.socialchat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SocialChat.db";
    private static final int DATABASE_VERSION = 2; // Incremented database version

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create a table for messages
        String CREATE_MESSAGES_TABLE = "CREATE TABLE messages ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "message TEXT,"
                + "image_uri TEXT)";
        db.execSQL(CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade logic
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE messages ADD COLUMN image_uri TEXT");
        }
    }

    // CRUD operations will be added here later
}
