package com.principal.dallamada.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.principal.dallamada.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<String> messagesData;
    private List<String> userData;

    private static final String TAG = "ChatAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView username;
        private final ImageView imageProfile;
        private final Context context;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = view.findViewById(R.id.textAdapterChat);
            username = view.findViewById(R.id.usernameAdapterChat);
            imageProfile = view.findViewById(R.id.imageProfileAdapterChat);
            context = view.getContext();
        }

        public Context getContext() {
            return context;
        }

        public TextView getUsername() {
            return username;
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public ChatAdapter(List<String> messages, List<String> user) {
        messagesData = messages;
        userData = user;


    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_chat, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        viewHolder.getTextView().setText(messagesData.get(position));
        viewHolder.getUsername().setText(userData.get(position).split("#")[0] + ":");

        mFirestore.collection("users").whereEqualTo("username", userData.get(position))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                        String img = "https://firebasestorage.googleapis.com/v0/b/dallamada.appspot.com/o/din.png?alt=media&token=8b57121c-ce71-4554-b1d7-8383559ea7ed";

                        for (QueryDocumentSnapshot doc : value) {
                            img = doc.getString("imgProfile");
                        }
                        Glide.with(viewHolder.getContext()).load(img).into(viewHolder.imageProfile);
                    }
                });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return messagesData.size();
    }
}