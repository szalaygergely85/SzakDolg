package com.example.szakdolg.chat.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.szakdolg.MyEditText;
import com.example.szakdolg.R;
import com.example.szakdolg.chat.adapter.UserAdapter;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.contacts.ContactsApiHelper;
import com.example.szakdolg.conversation.ConversationApiHelper;
import com.example.szakdolg.user.entity.User;
import com.example.szakdolg.util.SharedPreferencesUtil;
import java.util.ArrayList;
import java.util.List;

public class NewChatActivity extends AppCompatActivity {

   private MultiAutoCompleteTextView recipientInput;
   private UserAdapter dropdownAdapter;
   private List<User> contacts;

   private ConversationApiHelper conversationApiHelper = new ConversationApiHelper();
   private ContactsApiHelper contactsApiHelper = new ContactsApiHelper();
   private Button btnSend;
   private MyEditText edtMess;

   private String _token;

   private User currentUser;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_new_chat);

      _setToolbar();
      initView();

      _token =
      SharedPreferencesUtil.getStringPreference(
         this,
         SharedPreferencesConstans.USERTOKEN
      );

      currentUser =
              (User) this.getIntent()
                      .getSerializableExtra(SharedPreferencesConstans.CURRENT_USER);

      contacts = new ArrayList<>();

      dropdownAdapter =
      new UserAdapter(this, android.R.layout.simple_list_item_1, contacts);
      recipientInput.setAdapter(dropdownAdapter);
      recipientInput.setTokenizer(
         new MultiAutoCompleteTextView.CommaTokenizer()
      );
      recipientInput.setThreshold(1);

      contactsApiHelper.getContactsToMultiTextView(
         this,
         _token,
         new ContactsApiHelper.ContactsCallback() {
            @Override
            public void onContactsFetched(List<User> newContacts) {
               contacts.clear();
               contacts.addAll(newContacts);
               dropdownAdapter.updateUsers(newContacts);
            }
         }
      );

      setListeners();
   }

   private void setListeners() {
      btnSend.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String recipients = recipientInput.getText().toString();
               List<Long> recipientIds = new ArrayList<>();
               for (String recipient : recipients.split(", ")) {
                  Long id = Long.valueOf(extractIdFromDisplayName(recipient.trim()));
                  if (id != null) {
                     recipientIds.add(id);
                  }
               }

               recipientIds.add(currentUser.getUserId());
               conversationApiHelper.addNewConversationAndSendMessage(recipientIds, edtMess.getText().toString(), _token, currentUser, NewChatActivity.this);
            }
         }
      );

      recipientInput.addTextChangedListener(
         new TextWatcher() {
            @Override
            public void beforeTextChanged(
               CharSequence s,
               int start,
               int count,
               int after
            ) {}

            @Override
            public void onTextChanged(
               CharSequence s,
               int start,
               int before,
               int count
            ) {}

            @Override
            public void afterTextChanged(Editable s) {
               validateLastRecipient();
            }
         }
      );
   }

   private void initView() {
      recipientInput = findViewById(R.id.recipient_input);
      btnSend = findViewById(R.id.btnNewChatSend);
      edtMess = findViewById(R.id.edtNewChatMes);
   }

   private void _setToolbar() {
      Toolbar mToolbar = findViewById(R.id.newChatToolbar);
      mToolbar.setTitle("New Message");
      setSupportActionBar(mToolbar);
   }

   private void validateLastRecipient() {
      String text = recipientInput.getText().toString();
      if (text.endsWith(", ")) {
         String[] recipients = text.split(", ");
         String lastRecipient = recipients[recipients.length - 1].trim();
         if (!isValidRecipient(lastRecipient)) {
            // Remove the invalid recipient
            StringBuilder validRecipients = new StringBuilder();
            for (int i = 0; i < recipients.length - 1; i++) {
               validRecipients.append(recipients[i]).append(", ");
            }
            recipientInput.removeTextChangedListener(
               validateRecipientInputWatcher
            );
            recipientInput.setText(validRecipients.toString());
            recipientInput.setSelection(validRecipients.length());
            recipientInput.addTextChangedListener(
               validateRecipientInputWatcher
            );
            Toast
               .makeText(this, "Invalid recipient removed", Toast.LENGTH_SHORT)
               .show();
         } else {
            // Optional: Change text color for existing contacts
            recipientInput.setTextColor(
               isValidRecipient(lastRecipient) ? Color.BLUE : Color.BLACK
            );
         }
      }
   }

   private boolean isValidRecipient(String recipient) {
      for (User user : contacts) {
         if (user.getDisplayName().equals(recipient)) {
            return true;
         }
      }
      return false;
   }

   private String extractIdFromDisplayName(String displayName) {
      for (User user : contacts) {
         if (user.getDisplayName().equals(displayName)) {
            return user.getUserId().toString();
         }
      }
      return null;
   }

   private TextWatcher validateRecipientInputWatcher = new TextWatcher() {
      @Override
      public void beforeTextChanged(
         CharSequence s,
         int start,
         int count,
         int after
      ) {}

      @Override
      public void onTextChanged(
         CharSequence s,
         int start,
         int before,
         int count
      ) {}

      @Override
      public void afterTextChanged(Editable s) {
         validateLastRecipient();
      }
   };
}
