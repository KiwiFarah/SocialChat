package uk.ac.wlv.socialchat;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MessageDetailActivity extends AppCompatActivity {

    private TextView textViewMessageDetail;
    private DatabaseHelper dbHelper;
    private int messageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        textViewMessageDetail = findViewById(R.id.textViewMessageDetail);
        Button buttonDelete = findViewById(R.id.button_delete);
        dbHelper = new DatabaseHelper(this);

        messageId = getIntent().getIntExtra("message_id", -1);
        loadMessageDetail(messageId); // A method to load details from the database

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMessage(messageId);
            }
        });
    }

    private void loadMessageDetail(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("messages", new String[]{"id", "message"}, "id=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            int messageIndex = cursor.getColumnIndex("message");
            textViewMessageDetail.setText(cursor.getString(messageIndex));
        }
        cursor.close();
    }
    private void deleteMessage(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("messages", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        finish(); // Close the activity and return to the previous one
    }
}
