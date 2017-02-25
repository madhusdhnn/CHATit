package com.example.geekymad.blog;


import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ChatHolder extends RecyclerView.ViewHolder {

    private final TextView messageTextView;
    private final TextView emailTextView;
    private final LinearLayout mLinearLayout;
    private final RelativeLayout mMessageContainer;
   // private final FrameLayout mLeftArrow;
    //private final FrameLayout mRightArrow;
    private final int mGreen300;
    private final int mGray300;

    public ChatHolder(View itemView) {
        super(itemView);
        messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        emailTextView = (TextView) itemView.findViewById(R.id.emailTextView);
        mLinearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
       // mLeftArrow = (FrameLayout) itemView.findViewById(R.id.left_arrow);
        //mRightArrow = (FrameLayout) itemView.findViewById(R.id.right_arrow);
        mMessageContainer = (RelativeLayout) itemView.findViewById(R.id.message_container);
        mGreen300 = ContextCompat.getColor(itemView.getContext(), R.color.material_green_300);
        mGray300 = ContextCompat.getColor(itemView.getContext(), R.color.material_gray_300);
    }

     public void isSender(boolean isSender){
        final int color;
        if(isSender){
            color = mGreen300;
            //mRightArrow.setVisibility(View.VISIBLE);
            //mLeftArrow.setVisibility(View.GONE);
            mMessageContainer.setGravity(Gravity.END);
        }else{
            color = mGray300;
            //mLeftArrow.setVisibility(View.VISIBLE);
           // mRightArrow.setVisibility(View.GONE);
            mMessageContainer.setGravity(Gravity.START);
        }
        ((GradientDrawable) mLinearLayout.getBackground()).setColor(color);
       /* ((RotateDrawable) mLeftArrow.getBackground()).getDrawable()
                .setColorFilter(color, PorterDuff.Mode.SRC);

        ((RotateDrawable) mRightArrow.getBackground()).getDrawable()
                .setColorFilter(color, PorterDuff.Mode.SRC);*/
    }

    public void setEmail(String email){
        emailTextView.setText(email);
    }

    public void setText(String text){
        messageTextView.setText(text);
    }
}
