package com.principal.dallamada;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Button sendEvent = findViewById(R.id.btn_SendEvent);
        CalendarView calenderCreate = findViewById(R.id.calendarCreateEvent);

        EditText titleCreateEvent = findViewById(R.id.etTitleCreateEvent);
        EditText textCreateEvent = findViewById(R.id.etTextCreateEvent);

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        calenderCreate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                date = year + "/" + month + "/" + dayOfMonth;
            }
        });

        sendEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirestore.collection("users")
                        .document(user.getEmail()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                DocumentSnapshot document = task.getResult();
                                SimpleDateFormat formating = new SimpleDateFormat("dd/MM/yyyy");

                                Map<String, Object> event = new HashMap<>();
                                event.put("sounded", false);
                                event.put("user", document.getString("username"));
                                event.put("title", titleCreateEvent.getText().toString());
                                event.put("text", textCreateEvent.getText().toString());
                                event.put("date", date);

                                mFirestore.collection("groups").document(document.getString("selectedGroup")).collection("events").document().set(event);
                                finish();
                            }
                        });
            }
        });
    }
}