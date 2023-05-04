

package dm2e.jihad.chatty.model;



import static dm2e.jihad.chatty.chat.ChatWindowActivity.reciverIImg;
import static dm2e.jihad.chatty.chat.ChatWindowActivity.senderImg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import dm2e.jihad.chatty.security.EncriptDecript;
import dm2e.jihad.chatty.R;

public class MessagesAdpter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Messege> messagesAdpterArrayList;
    int ITEM_SEND=1;
    int ITEM_RECIVE=2;

    public MessagesAdpter(Context context, ArrayList<Messege> messagesAdpterArrayList) {
        this.context = context;
        this.messagesAdpterArrayList = messagesAdpterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderVierwHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout, parent, false);
            return new reciverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Messege messages = messagesAdpterArrayList.get(position);

// desencriptar
        try {
        if (holder.getClass()==senderVierwHolder.class){
                senderVierwHolder viewHolder = (senderVierwHolder) holder;
                viewHolder.msgtxt.setText( EncriptDecript.decrypt(messages.getMessage()));
               // viewHolder.senderTime.setText( messages.getTimeStamp());
                Picasso.get().load(senderImg).into(viewHolder.circleImageView);
        }else { reciverViewHolder viewHolder = (reciverViewHolder) holder;
            viewHolder.msgtxt.setText(EncriptDecript.decrypt(messages.getMessage()));
            Picasso.get().load(reciverIImg).into(viewHolder.circleImageView);

        }
        } catch (Exception e) {
        e.printStackTrace();
    }
    }

    @Override
    public int getItemCount() {
        return messagesAdpterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messege messages = messagesAdpterArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderid())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECIVE;
        }
    }

    class   senderVierwHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt;
        public senderVierwHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profilerggg);
            msgtxt = itemView.findViewById(R.id.msgsendertyp);


        }
    }
    class reciverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt;
        public reciverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.pro);
            msgtxt = itemView.findViewById(R.id.recivertextset);

        }
    }
}
