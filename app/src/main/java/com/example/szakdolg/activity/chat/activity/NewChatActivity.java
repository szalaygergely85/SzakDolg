package com.example.szakdolg.activity.chat.activity;

import com.example.szakdolg.activity.base.BaseActivity;

@Deprecated
public class NewChatActivity extends BaseActivity {
   /*

private MultiAutoCompleteTextView recipientInput;
private UserAdapter dropdownAdapter;
private List<User> contacts;

NewChatActivityHelper newChatActivityHelper;
private ConversationApiHelper conversationApiHelper;
private ContactsApiHelper contactsApiHelper = new ContactsApiHelper();
private Button btnSend;
private MyEditText edtMess;

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_new_chat);

	_setToolbar();

	_initView();

	newChatActivityHelper = new NewChatActivityHelper(this, currentUser);

	conversationApiHelper = new ConversationApiHelper(this, currentUser);

	contacts = newChatActivityHelper.getContacts();

	dropdownAdapter =
	new UserAdapter(this, android.R.layout.simple_list_item_1, contacts);
	recipientInput.setAdapter(dropdownAdapter);
	recipientInput.setTokenizer(
		new MultiAutoCompleteTextView.CommaTokenizer()
	);
	recipientInput.setThreshold(1);

	setListeners();
}

private void setListeners() {
	btnSend.setOnClickListener(view -> {
		String recipients = recipientInput.getText().toString();
		newChatActivityHelper.sendMessage(
			recipients,
			edtMess.getText().toString()
		);
	});

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

private void _initView() {
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
};*/
}
