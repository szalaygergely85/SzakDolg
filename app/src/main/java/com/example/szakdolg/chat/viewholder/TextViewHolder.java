package com.example.szakdolg.chat.viewholder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.R;
import com.example.szakdolg.user.model.User;

public class TextViewHolder extends RecyclerView.ViewHolder {

   private final TextView txtText;
   private final TextView txtTimeIn;
   private final TextView txtTimeOut;
   private final TextView txtTextFrMe;
   private final RelativeLayout relIn;
   private final RelativeLayout relOut;

   public TextViewHolder(View itemView) {
      super(itemView);
      txtText = itemView.findViewById(R.id.chatText);
      txtTextFrMe = itemView.findViewById(R.id.chatTextFrMe);
      txtTimeIn = itemView.findViewById(R.id.chatTextTimeIn);
      txtTimeOut = itemView.findViewById(R.id.chatTextTimeOut);
      relIn = itemView.findViewById(R.id.chatRelIn);
      relOut = itemView.findViewById(R.id.chatRelOut);
   }

   public void bind(
      String decryptedContentString,
      String timeForm,
      Long senderId,
      User currentUser
   ) {
      txtTimeOut.setText(timeForm);

      if (senderId.equals(currentUser.getUserId())) {
         relIn.setVisibility(View.GONE);
         relOut.setVisibility(View.VISIBLE);
         txtTextFrMe.setText(decryptedContentString);
      } else {
         relOut.setVisibility(View.GONE);
         relIn.setVisibility(View.VISIBLE);
         txtText.setText(decryptedContentString);
      }
   }
}
