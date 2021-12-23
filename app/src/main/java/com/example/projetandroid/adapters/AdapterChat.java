package com.example.projetandroid.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projetandroid.models.ModelChat;
import com.example.projetandroid.R;
import com.example.projetandroid.testeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    String imageUrl;
    FirebaseUser fUser;
    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
      //  this.imageUrl = imageUrl;
    }
    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views
       // ImageView profileIv;
        TextView messageTv;
        TextView timeTv;
        TextView isSeenTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //init views
        //    profileIv = itemView.findViewById(R.id.profileIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
        }
    }
    @NonNull
    @Override
   public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layouts : row_chat_right for sender,row_chat_left for receiver
        if (viewType==MSG_TYPE_RIGHT){

            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent,false);
            return new MyHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent,false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String message =chatList.get(position).getMessage();
        String timestamp =chatList.get(position).getTimestamp();


        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp)); /* this code is first error*/
        String dataTime = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();


   /*    ParsePosition pos = new ParsePosition(position);
        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date stringDate = simpledateformat.parse(timestamp, pos);
        String  dateTime=  stringDate.toString();*/

        //set data
        holder.messageTv.setText(message);
       holder.timeTv.setText(dataTime);

        try {
          //  Picasso.get().load(imageUrl).into(holder.profileIv);
        }
        catch (Exception e){

        }
        //set seen/delivred status of message
        if(position==chatList.size()-1){
            if (chatList.get(position).isSeen()){
                holder.isSeenTv.setText("seen");
            }
            else{
                holder.isSeenTv.setText("Delevred");
            }
        }
        else {
            holder.isSeenTv.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently signed in user
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

}