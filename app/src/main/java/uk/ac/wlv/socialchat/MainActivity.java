package uk.ac.wlv.socialchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private EditText editTextMessage;
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);
        editTextMessage = findViewById(R.id.editTextMessage);
        Button buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMessage();
            }
        });
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        Button buttonDeleteSelected = findViewById(R.id.button_delete_selected);
        buttonDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedMessages();
            }
        });
        Button buttonSelectImage = findViewById(R.id.button_select_image);
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        loadMessages();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Log.d("MainActivity", "Selected Image URI: " + imageUri);
        }
    }

    private void deleteSelectedMessages() {
        List<Message> selectedMessages = messageAdapter.getSelectedMessages();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            for (Message message : selectedMessages) {
                db.delete("messages", "id = ?", new String[]{String.valueOf(message.getId())});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("MainActivity", "Error deleting messages", e);
        } finally {
            db.endTransaction();
        }
        db.close();

        // Reload messages after deletion
        loadMessages();
    }

    private void loadMessages() {
        List<Message> newMessages = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("messages", new String[]{"id", "message", "image_uri"}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex("id");
            int messageIndex = cursor.getColumnIndex("message");
            int imageUriIndex = cursor.getColumnIndex("image_uri");
            String imageUri = cursor.getString(imageUriIndex);
            Message message = new Message(cursor.getInt(idIndex), cursor.getString(messageIndex));
            message.setImageUri(imageUri); // Set the image URI in the message object
            newMessages.add(message);
        }
        cursor.close();

        messageAdapter.updateMessages(newMessages);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadMessages(); // Refresh the messages list
    }


    private void saveMessage() {
        String messageText = editTextMessage.getText().toString();
        if (!messageText.isEmpty()) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("message", messageText);
            if (imageUri != null) {
                values.put("image_uri", imageUri.toString());
            }
            db.insert("messages", null, values);
            db.close();
            editTextMessage.setText(""); // Clear the input
            imageUri = null; // Reset the image URI

            loadMessages();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // You can handle query submit action here if needed
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Call a method in the adapter to perform the search
        Log.d("Search", "Filtering with text: " + newText);
        messageAdapter.filter(newText);
        return true;
    }

}
