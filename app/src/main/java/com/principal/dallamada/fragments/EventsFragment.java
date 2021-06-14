package com.principal.dallamada.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.principal.dallamada.CreateEventActivity;
import com.principal.dallamada.R;
import com.principal.dallamada.adapters.EventAdapter;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventsFragment extends Fragment {

    private FirebaseFirestore mFirestore;
    private static final String TAG = "EventFragment";

    private RecyclerView eventRecycler;
    private EventAdapter eventAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_events, container, false);
        Button sendEvent = v.findViewById(R.id.btn_eventSend);
        CalendarView calendarView = v.findViewById(R.id.eventCalendar);

        List<EventDay> events = new ArrayList<>();

        mFirestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mFirestore.collection("users")
                .document(user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentSnapshot e = task.getResult();
                        mFirestore.collection("groups").document(e.getString("selectedGroup")).collection("events")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                                        String dateFormat = "";

                                        for (QueryDocumentSnapshot doc : value) {
                                            Calendar calendar = Calendar.getInstance();

                                            dateFormat = doc.getString("date");
                                            int year = Integer.parseInt(dateFormat.split("/")[0]);
                                            int month = Integer.parseInt(dateFormat.split("/")[1]);
                                            int day = Integer.parseInt(dateFormat.split("/")[2]);

                                            calendar.set(year, month, day);
                                            events.add(new EventDay(calendar, R.drawable.phone));
                                        }
                                        calendarView.setEvents(events);
                                    }
                                });
                    }
                });

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                Log.w(TAG, clickedDayCalendar.get(Calendar.YEAR) + "/" + clickedDayCalendar.get(Calendar.MONTH) + "/" + clickedDayCalendar.get(Calendar.DAY_OF_MONTH));

                mFirestore.collection("users")
                        .document(user.getEmail()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                DocumentSnapshot e = task.getResult();
                                mFirestore.collection("groups").document(e.getString("selectedGroup")).collection("events")
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {


                                                List<String> title = new ArrayList<>();
                                                List<String> text = new ArrayList<>();
                                                String clickedDate = clickedDayCalendar.get(Calendar.YEAR) + "/" +
                                                        clickedDayCalendar.get(Calendar.MONTH) + "/" +
                                                        clickedDayCalendar.get(Calendar.DAY_OF_MONTH);


                                                for (QueryDocumentSnapshot doc : value) {
                                                    if (clickedDate.equals(doc.getString("date"))) {
                                                        title.add(doc.getString("title"));
                                                        text.add(doc.getString("text"));
                                                    }
                                                }

                                                eventRecycler = v.findViewById(R.id.recyclerEvents);
                                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                                eventRecycler.setLayoutManager(layoutManager);
                                                eventAdapter = new EventAdapter(text, title);
                                                eventRecycler.setAdapter(eventAdapter);

                                            }
                                        });
                            }
                        });
            }
        });

        sendEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateEventActivity.class);
                startActivity(intent);


            }
        });
        return v;
    }
}