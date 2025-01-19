package com.example.szakdolg.activity.chat.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.szakdolg.R;
import com.example.szakdolg.models.image.util.ImageUtil;
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
   private final LinearLayout lLIn;
   private final LinearLayout lLout;

   public TextViewHolder(View itemView) {
      super(itemView);
      txtText = itemView.findViewById(R.id.chatText);
      txtTextFrMe = itemView.findViewById(R.id.chatTextFrMe);
      txtTimeIn = itemView.findViewById(R.id.chatTextTimeIn);
      txtTimeOut = itemView.findViewById(R.id.chatTextTimeOut);
      lLIn = itemView.findViewById(R.id.chatLLin);
      lLout = itemView.findViewById(R.id.chatLLout);
      dateTextView = itemView.findViewById(R.id.dateTextView);

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
      boolean shouldShowProfilePicture,
      Context context
   ) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String dateString = dateFormat.format(new Date(time));

      dateTextView.setVisibility(showDateSeparator ? View.VISIBLE : View.GONE);
      dateTextView.setText(dateString);
      if (senderId.equals(currentUser.getUserId())) {
         imageView.setVisibility(View.GONE);
         //My Messages
         lLIn.setVisibility(View.GONE);
         lLout.setVisibility(View.VISIBLE);

         txtTimeIn.setVisibility(View.GONE);
         txtTimeOut.setVisibility(
            shouldShowProfilePicture ? View.VISIBLE : View.GONE
         );
         txtTimeOut.setText(timeForm);
         txtTextFrMe.setBackground(
            shouldShowProfilePicture
               ? ContextCompat.getDrawable(
                  context,
                  R.drawable.bg_chat_one_sided_grey
               )
               : ContextCompat.getDrawable(context, R.drawable.bg_chat_grey)
         );
         txtTextFrMe.setText(decryptedContentString);
      } else {
         imageView.setVisibility(
            shouldShowProfilePicture ? View.VISIBLE : View.INVISIBLE
         );

         String imageUrl = ImageUtil.buildProfileImageUrl(senderId);
         if (imageUrl != null) {
            Glide
               .with(context)
               .load(imageUrl)
               .placeholder(R.drawable.ic_blank_profile)
               .error(R.drawable.ic_blank_profile)
               .into(imageView);
         } else {
            imageView.setImageResource(R.drawable.ic_blank_profile);
         }

         lLout.setVisibility(View.GONE);

         lLIn.setVisibility(View.VISIBLE);

         txtTimeOut.setVisibility(View.GONE);

         txtTimeIn.setVisibility(
            shouldShowProfilePicture ? View.VISIBLE : View.GONE
         );
         txtTimeIn.setText(timeForm);

         txtTextFrMe.setVisibility(View.GONE);

         txtText.setText(decryptedContentString);
         txtText.setVisibility(View.VISIBLE);
         txtText.setBackground(
            shouldShowProfilePicture
               ? ContextCompat.getDrawable(
                  context,
                  R.drawable.bg_chat_one_sided_white
               )
               : ContextCompat.getDrawable(context, R.drawable.bg_chat_white)
         );
      }
   }
}
