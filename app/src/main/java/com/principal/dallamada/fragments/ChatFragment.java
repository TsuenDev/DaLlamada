package com.principal.dallamada.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.principal.dallamada.R;
import com.principal.dallamada.adapters.ChatAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {


    private FirebaseFirestore mFirestore;

    private EditText sendMessageInput;
    private ImageButton sendMessageButton;
    RecyclerView chatRecycler;
    ChatAdapter chatAdapter;

    private static final String TAG = "CHAT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat, container, true);


        sendMessageInput = v.findViewById(R.id.inputMessageChat);
        sendMessageButton = v.findViewById(R.id.buttonMessageChat);

        mFirestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        mFirestore.collection("users")
                .document(user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentSnapshot document = task.getResult();

                        // Método que recoge los ultimos mensajes y los pone en pantalla
                        mFirestore.collection("groups")
                                .document(document.getString("selectedGroup"))
                                .collection("messages")
                                .orderBy("time", Query.Direction.DESCENDING)
                                .limit(50)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Log.w(TAG, "Listen failed.", e);
                                            return;
                                        }

                                        List<String> messages = new ArrayList<>();
                                        List<String> users = new ArrayList<>();


                                        for (QueryDocumentSnapshot doc : value) {
                                            if (doc.get("text") != null) {

                                                users.add(doc.getString("user"));
                                                messages.add(doc.getString("text"));

                                            }
                                        }

                                        //Se añade el adapter al recyclerView
                                        chatRecycler = v.findViewById(R.id.chatReclycler);
                                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                        chatRecycler.setLayoutManager(layoutManager);
                                        chatAdapter = new ChatAdapter(messages, users);
                                        chatRecycler.setAdapter(chatAdapter);

                                    }
                                });
                    }
                });


        //Metodo recoge el texto del input y el tiempo en milisegundos y los añade al grupo de la persona
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirestore.collection("users")
                        .document(user.getEmail()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                DocumentSnapshot document = task.getResult();

                                if (sendMessageInput.getText().length() == 0) {

                                } else {
                                    Map<String, Object> message = new HashMap<>();
                                    message.put("time", System.currentTimeMillis());
                                    message.put("user", document.getString("username"));
                                    message.put("text", sendMessageInput.getText().toString());
                                    message.put("imgProfile", document.getString("imgProfile"));


                                    mFirestore.collection("groups")
                                            .document(document.getString("selectedGroup"))
                                            .collection("messages")
                                            .document()
                                            .set(message);
                                    sendMessageInput.getText().clear();
                                }
                            }

                        });
            }
        });

        return v;
    }


}