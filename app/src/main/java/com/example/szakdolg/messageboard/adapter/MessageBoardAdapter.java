package com.example.szakdolg.messageboard.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.R;
import com.example.szakdolg.constans.MessageTypeConstants;
import com.example.szakdolg.db.util.ConversationDatabaseUtil;
import com.example.szakdolg.db.util.MessageDatabaseUtil;
import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.model.conversation.ConversationApiHelper;
import com.example.szakdolg.model.conversation.entity.Conversation;
import com.example.szakdolg.model.conversation.entity.ConversationParticipant;
import com.example.szakdolg.model.message.entity.MessageEntry;
import com.example.szakdolg.model.user.model.User;
import com.example.szakdolg.util.EncryptionHelper;
import com.example.szakdolg.util.KeyStoreUtil;
import java.util.ArrayList;
import java.util.List;

public class MessageBoardAdapter
   extends RecyclerView.Adapter<MessageBoardAdapter.ViewHolder> {

   private List<Conversation> conversationList = new ArrayList<>();
   private final Context context;
   private User currentUser;
   private static final String TAG = "MessageBoardAdapter";

   private String _token;

   private UserDatabaseUtil userDatabaseUtil;
   private MessageDatabaseUtil messageDatabaseUtil;
   private ConversationDatabaseUtil conversationDatabaseUtil;

   ConversationApiHelper conversationApiHelper = new ConversationApiHelper();

   public MessageBoardAdapter(
      Context mContext,
      String token,
      User currentUser
   ) {
      this.context = mContext;
      this._token = token;
      this.currentUser = currentUser;
      this.userDatabaseUtil = new UserDatabaseUtil(mContext, currentUser);
      this.messageDatabaseUtil = new MessageDatabaseUtil(mContext, currentUser);
      this.conversationDatabaseUtil =
      new ConversationDatabaseUtil(mContext, currentUser);
   }

   /*
	public void setImageView(String uID, Context context, ImageView image) {
		Uri picUri = null;
		Log.d(TAG, "getPicURl: " + uID);
		try {
			picUri = FileHandling.getUri(uID, context);
		} catch (Exception e) {
			Log.d(TAG, "setImageView: " + e);
		}
		if (picUri == null) {
			Log.d(TAG, "setImageView: couldn't find the pic");
			try {
				storageRef.child(uID + ".jpg").getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
					@Override
					public void onComplete(@NonNull Task<StorageMetadata> task) {
						if (task.isSuccessful()) {

							storageRef.child(uID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
								@Override
								public void onSuccess(Uri uri) {
									Log.d(TAG, "getPicURl: " + uri);
									Glide.with(context)
											.asBitmap()
											.load(uri)
											.into(new CustomTarget<Bitmap>() {
												@Override
												public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
													try {
														FileHandling.saveImageFile(uID, resource, context);
													} catch (IOException e) {
														e.printStackTrace();
													}
												}

												@Override
												public void onLoadCleared(@Nullable Drawable placeholder) {
												}
											});

									Glide.with(context)
											.asBitmap()
											.load(uri)
											.into(image);
								}
							}).addOnFailureListener(new OnFailureListener() {
								@Override
								public void onFailure(@NonNull Exception exception) {
									image.setImageResource(R.drawable.ic_blank_profile);
									Log.d(TAG, "onFailure: " + exception);
								}
							});
						}
					}
				});

			} catch (Exception e) {
				Log.d(TAG, "setImageView: " + e);
			}

		} else {
			Log.d(TAG, "setImageView: " + picUri);
			image.setImageURI(FileHandling.getUri(uID, context));
		}
	}*/

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent,
      int viewType
   ) {
      View view = LayoutInflater
         .from(parent.getContext())
         .inflate(R.layout.message_board_item, parent, false);
      ViewHolder holder = new ViewHolder(view);
      return holder;
   }

   @Override
   public void onBindViewHolder(
      @NonNull ViewHolder holder,
      @SuppressLint("RecyclerView") int position
   ) {
      Conversation conversation = conversationList.get(position);

      holder.txtName.setText(conversation.getConversationName());

      MessageEntry messageEntry = messageDatabaseUtil.getLatestMessageEntry(
         conversation.getConversationId()
      );

      if (messageEntry.getContent() != null) {
         List<ConversationParticipant> participants =
            conversationDatabaseUtil.getParticipantsByConversationId(
               conversation.getConversationId()
            );
         User participant = userDatabaseUtil.getUserById(
            participants.get(0).getUserId()
         );

         if (messageEntry.isRead() || isSenderLoggedUser(messageEntry)) {
            holder.txtMessage.setTypeface(null, Typeface.NORMAL);
            holder.txtName.setTypeface(null, Typeface.NORMAL);
         } else {
            holder.txtMessage.setTypeface(null, Typeface.BOLD);
            holder.txtName.setTypeface(null, Typeface.BOLD);
         }
         if (messageEntry.getType() == MessageTypeConstants.MESSAGE) {
            String decryptedContentString = null;
            try {
               if (isSenderLoggedUser(messageEntry)) {
                  decryptedContentString =
                  EncryptionHelper.decrypt(
                     messageEntry.getContentSenderVersion(),
                     KeyStoreUtil.getPrivateKeyFromFile(context, currentUser)
                  );
               } else {
                  decryptedContentString =
                  EncryptionHelper.decrypt(
                     messageEntry.getContent(),
                     KeyStoreUtil.getPrivateKeyFromFile(context, currentUser)
                  );
               }

               holder.txtMessage.setText(decryptedContentString);
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         }
         if (messageEntry.getType() == MessageTypeConstants.IMAGE) {
            holder.txtMessage.setText("Image received from:");
         }
         holder.txtName.setText(participant.getDisplayName());

         // setImageView(messageB.get(position).getContactId(), mContext, holder.image);
         holder.parent.setOnClickListener(
            new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                  conversationApiHelper.openConversation(
                     conversation.getConversationId(),
                     context,
                     currentUser,
                     _token
                  );
               }
            }
         );
      }
   }

   @Override
   public int getItemCount() {
      return conversationList.size();
   }

   /*
public void setMessageB(ArrayList<MessageBoard> messageB) {
	this.messageB = messageB;
	notifyDataSetChanged();
}*/

   public void setConversationList(List<Conversation> conversationList) {
      this.conversationList = conversationList;
      notifyDataSetChanged();
   }

   public class ViewHolder extends RecyclerView.ViewHolder {

      private final TextView txtName;
      private final TextView txtMessage;
      private final RelativeLayout parent;
      private final ImageView image;

      public ViewHolder(@NonNull View itemView) {
         super(itemView);
         image = itemView.findViewById(R.id.mesBrdImage);
         txtName = itemView.findViewById(R.id.mesBrdName);
         txtMessage = itemView.findViewById(R.id.mesBrdMessage);
         parent = itemView.findViewById(R.id.parent);
      }
   }

   private boolean isSenderLoggedUser(MessageEntry messageEntry) {
      return currentUser.getUserId().equals(messageEntry.getSenderId());
   }
}
