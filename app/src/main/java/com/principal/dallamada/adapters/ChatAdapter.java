package com.principal.dallamada.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.principal.dallamada.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<String> messagesData;
    private List<String> userData;
    private List<String> imgProfileData;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView username;
        private final ImageView imageProfile;
        private final Context context;


        public ViewHolder(View view) {
            super( view );
            // Define click listener for the ViewHolder's View

            textView = view.findViewById( R.id.textAdapterChat );
            username = view.findViewById( R.id.usernameAdapterChat );
            imageProfile = view.findViewById( R.id.imageProfileAdapterChat );
            context = view.getContext();
        }

        public Context getContext() {
            return context;
        }

        public ImageView getImageProfile() {
            return imageProfile;
        }

        public TextView getUsername() {
            return username;
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public ChatAdapter(List<String> messages, List<String> user, List<String> imgProfile) {
        messagesData = messages;
        userData = user;
        imgProfileData = imgProfile;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from( viewGroup.getContext() )
                .inflate( R.layout.adapter_chat, viewGroup, false );

        return new ViewHolder( view );
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText( messagesData.get( position )  );
        viewHolder.getUsername().setText( userData.get( position )+ ":" );
        Glide.with( viewHolder.getContext() )
                .load( imgProfileData.get( position ) )
                .into( viewHolder.imageProfile );


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return messagesData.size();
    }
}