package com.principal.dallamada.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.principal.dallamada.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private static final String TAG = "ChatAdapter";

    private List<String> messagesData;
    private List<String> titleData;

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

    public EventAdapter(List<String> text, List<String> title) {

        messagesData = text;
        titleData = title;


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

        viewHolder.getTextView().setText(messagesData.get(position));
        viewHolder.getUsername().setText(titleData.get(position));


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return messagesData.size();
    }
}