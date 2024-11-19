package com.example.szakdolg.activity.chat.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.R;
import com.example.szakdolg.models.user.entity.User;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextViewHolder extends RecyclerView.ViewHolder {

   private final ImageView imageView;

   private final TextView txtText;
   private final TextView txtTimeIn;
   private final TextView txtTimeOut;
   private final TextView txtTextFrMe;
   private final TextView dateTextView;
   private final RelativeLayout relIn;
   private final RelativeLayout relOut;

   private final LinearLayout dateSeparator;

   public TextViewHolder(View itemView) {
      super(itemView);
      txtText = itemView.findViewById(R.id.chatText);
      txtTextFrMe = itemView.findViewById(R.id.chatTextFrMe);
      txtTimeIn = itemView.findViewById(R.id.chatTextTimeIn);
      txtTimeOut = itemView.findViewById(R.id.chatTextTimeOut);
      relIn = itemView.findViewById(R.id.chatRelIn);
      relOut = itemView.findViewById(R.id.chatRelOut);
      dateTextView = itemView.findViewById(R.id.dateTextView);
      dateSeparator = itemView.findViewById(R.id.dateSeparatorContainer);
      imageView = itemView.findViewById(R.id.profilePicIn);
   }

   public void bind(
      String decryptedContentString,
      String timeForm,
      Long senderId,
      User currentUser,
      boolean showDateSeparator,
      long time,
      boolean shouldShowTime,
      boolean shouldShowProfilePicture
   ) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String dateString = dateFormat.format(new Date(time));
      txtTimeOut.setText(timeForm);
      txtTimeIn.setText(timeForm);

      if (shouldShowProfilePicture) {
         imageView.setVisibility(View.VISIBLE);
      } else {
         imageView.setVisibility(View.GONE);
         RelativeLayout.LayoutParams params =
            (RelativeLayout.LayoutParams) txtText.getLayoutParams();

         params.setMarginStart(50);

         txtText.setLayoutParams(params);
      }

      if (shouldShowTime) {
         txtTimeOut.setVisibility(View.VISIBLE);
         txtTimeIn.setVisibility(View.VISIBLE);
      } else {
         txtTimeOut.setVisibility(View.GONE);
         txtTimeIn.setVisibility(View.GONE);
      }

      if (senderId.equals(currentUser.getUserId())) {
         relIn.setVisibility(View.GONE);
         relOut.setVisibility(View.VISIBLE);
         txtTextFrMe.setText(decryptedContentString);
      } else {
         relOut.setVisibility(View.GONE);
         relIn.setVisibility(View.VISIBLE);
         txtText.setText(decryptedContentString);
      }

      if (showDateSeparator) {
         dateSeparator.setVisibility(View.VISIBLE); // Show the separator
         dateTextView.setText(dateString); // Set the date string
      } else {
         dateSeparator.setVisibility(View.GONE); // Hide the separator
      }
   }
}
