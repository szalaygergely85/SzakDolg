package com.example.szakdolg.activity;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.example.szakdolg.util.KeyStoreUtil.getPrivateKey;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.DTO.MessageBoard;

import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.recviewadapter.MessageBoardRecAdapter;
import com.example.szakdolg.R;
import com.example.szakdolg.user.User;
import com.example.szakdolg.user.UserApiHelper;
import com.example.szakdolg.util.SharedPreferencesUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

public class MessageBoardActivity extends AppCompatActivity {
    private static final String TAG = "MessageB";
    private FloatingActionButton contactsButton;
    private RecyclerView messageBoardRecView;
    private MessageBoardRecAdapter adapter;
    private MaterialToolbar mToolbar;
    private User loggedUser;
    ArrayList<MessageBoard> messageBoard = new ArrayList<>();
    
    private MessageApiHelper messageApiHelper= new MessageApiHelper();

    private String userToken;
    
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board_actvitiy);

        _initView();

        loggedUser = (User) this.getIntent().getSerializableExtra(SharedPreferencesConstans.CURRENT_USER);
        userToken = SharedPreferencesUtil.getStringPreference(this, SharedPreferencesConstans.USERTOKEN);

        UserApiHelper a = new UserApiHelper();

        a._getAndSavePrivateKey(loggedUser, userToken);


        mToolbar = (MaterialToolbar) findViewById(R.id.messageBoardToolbar);
        setSupportActionBar(mToolbar);

        messageBoardRecView = findViewById(R.id.messageBoardRecView);
        
        adapter = new MessageBoardRecAdapter(this);
        adapter.setMessageB(messageBoard);
        adapter.setLoggedUser(loggedUser);

        messageBoardRecView.setAdapter(adapter);
        messageBoardRecView.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    protected void onStart() {
        super.onStart();

        messageApiHelper.getLatestMessages(adapter,userToken, loggedUser);

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageBoardActivity.this, ContactsActivity.class);
                intent.putExtra(SharedPreferencesConstans.CURRENT_USER, loggedUser);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menuProfile:
                intent = new Intent(MessageBoardActivity.this, ProfileActivity.class);
                intent.putExtra(SharedPreferencesConstans.CURRENT_USER, loggedUser);
                startActivity(intent);
                break;
            case R.id.menuContacts:
                intent = new Intent(MessageBoardActivity.this, ContactsActivity.class);
                intent.putExtra(SharedPreferencesConstans.CURRENT_USER, loggedUser);
                startActivity(intent);
                break;
            case R.id.menuSingOut:

            default:
                break;
        }
        return false;
    }
    private void _initView() {
        contactsButton = findViewById(R.id.btnMesBrdNew);
    }
}
