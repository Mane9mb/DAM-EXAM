package cat.udl.tidic.amd.dam_tips.views;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import cat.udl.tidic.amd.dam_tips.R;
import cat.udl.tidic.amd.dam_tips.models.Account;

class UserAdapter extends ListAdapter<Account, UserAdapter.UserHolder> {

    private final static String TAG = "UserAdapter";
    private OnItemClickListener eventItemListener;

    protected UserAdapter(@NonNull DiffUtil.ItemCallback<Account> diffCallback) {
        super(diffCallback);
    }


    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_items_users, parent, false);
        return new UserHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        Account current_player = (Account) getItem(position);
        holder.usernameTextView.setText(current_player.getUsername());
        holder.punts.setText(current_player.getPoints());



        Log.d(TAG, "Photo URl:" + current_player.getPhoto());

        //Picasso.get().load(current_player.getPhoto()).into(holder.photo);
    }

    public Account getUserAt(int position){
        Log.d(TAG, "Position: "+ position);
        Log.d(TAG, "username: "+ getItem(position).getUsername());
        return getItem(position);
    }

    class UserHolder extends RecyclerView.ViewHolder {
        private TextView usernameTextView;
        private TextView punts;
        private ImageView photo;
        private TextView text_punts;

        public UserHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.playerName);
            photo = itemView.findViewById(R.id.img_perf);
           text_punts = itemView.findViewById(R.id.text_punts);
           punts = itemView.findViewById(R.id.ed_punts);
//            left = itemView.findViewById(R.id.img_psIz);
//            rigth = itemView.findViewById(R.id.img_pscDere);
//            golpe = itemView.findViewById(R.id.text_golpuser);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (eventItemListener != null && position != RecyclerView.NO_POSITION) {
                        eventItemListener.onItemClick(getItem(position));
                    }
                }
            });
        }

    }
    public interface OnItemClickListener {
        void onItemClick(Account account);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.eventItemListener = listener;
    }
}
