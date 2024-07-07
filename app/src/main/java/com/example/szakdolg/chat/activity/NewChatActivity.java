package com.example.szakdolg.chat.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.szakdolg.MyEditText;
import com.example.szakdolg.R;

import java.util.ArrayList;
import java.util.Arrays;

public class NewChatActivity extends AppCompatActivity {

   private MultiAutoCompleteTextView recipientInput;
   private ArrayAdapter<String> dropdownAdapter;
   private ArrayList<String> contacts;
   private ArrayList<String> existingContacts;

   private Button btnSend;
   private MyEditText edtMess;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_new_chat);

      _setToolbar();
      initView();





      contacts = new ArrayList<>(Arrays.asList("Alice", "Bob", "Charlie", "Dave", "Eve"));
      existingContacts = new ArrayList<>(Arrays.asList("Alice", "Charlie", "Eve"));

      dropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contacts);

      recipientInput.setAdapter(dropdownAdapter);
      recipientInput.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

      recipientInput.setThreshold(1);

      setListeners();

   }

   private void setListeners() {
      recipientInput.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
         }

         @Override
         public void afterTextChanged(Editable s) {
            validateLastRecipient();
         }
      });
   }

   private void initView() {
      recipientInput = findViewById(R.id.recipient_input);
      btnSend = findViewById(R.id.btnNewChatSend);
      edtMess = findViewById(R.id.edtNewChatMes);
   }

   private void _setToolbar() {

      Toolbar mToolbar = (Toolbar) findViewById(R.id.newChatToolbar);
      mToolbar.setTitle("New Message");
      setSupportActionBar(mToolbar);
   }

   private void validateLastRecipient() {
      String text = recipientInput.getText().toString();
      if (text.endsWith(", ")) {
         String[] recipients = text.split(", ");
         String lastRecipient = recipients[recipients.length - 1].trim();
         if (!contacts.contains(lastRecipient)) {
            // Remove the invalid recipient
            StringBuilder validRecipients = new StringBuilder();
            for (int i = 0; i < recipients.length - 1; i++) {
               validRecipients.append(recipients[i]).append(", ");
            }
            recipientInput.removeTextChangedListener(validateRecipientInputWatcher);
            recipientInput.setText(validRecipients.toString());
            recipientInput.setSelection(validRecipients.length());
            recipientInput.addTextChangedListener(validateRecipientInputWatcher);
            Toast.makeText(this, "Invalid recipient removed", Toast.LENGTH_SHORT).show();
         } else {
            // Optional: Change text color for existing contacts
            recipientInput.setTextColor(existingContacts.contains(lastRecipient) ? Color.BLUE : Color.BLACK);
         }
      }
   }

   private TextWatcher validateRecipientInputWatcher = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {
         validateLastRecipient();
      }
   };
}
