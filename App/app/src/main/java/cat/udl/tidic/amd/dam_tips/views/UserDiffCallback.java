package cat.udl.tidic.amd.dam_tips.views;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import cat.udl.tidic.amd.dam_tips.models.Account;

public class UserDiffCallback extends DiffUtil.ItemCallback<Account> {

    @Override
    public boolean areItemsTheSame(@NonNull Account oldItem, @NonNull Account newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull Account oldItem, @NonNull Account newItem) {
        return oldItem.equals(newItem);
    }
}
