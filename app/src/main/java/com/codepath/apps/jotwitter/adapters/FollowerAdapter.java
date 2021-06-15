package com.codepath.apps.jotwitter.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.jotwitter.databinding.ItemUserBinding;
import com.codepath.apps.jotwitter.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {

    ItemUserBinding binding;
    Context context;
    List<User> users;

    public FollowerAdapter(Context context, List<User> users){
        this.context = context;
        this.users = users;
    }
    @NonNull
    @NotNull
    @Override
    public FollowerAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        binding = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FollowerAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivUserProfile;
        Button btnFollow;
        TextView tvScreenname;
        TextView tvUsername;
        EditText etBio;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivUserProfile = binding.ivUserPic;
            btnFollow = binding.btnFollow;
            tvScreenname = binding.tvScreenname;
            tvUsername = binding.tvUsername;
            etBio = binding.etBio;
        }

        public void bind(User user){
            Glide.with(context).load(user.getProfileUrl()).into(ivUserProfile);
            tvScreenname.setText(user.getName());
            tvUsername.setText(user.getUsername());
            etBio.setText(user.getBio());
        }
    }
}
