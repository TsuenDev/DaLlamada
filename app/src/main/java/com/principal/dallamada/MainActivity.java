package com.principal.dallamada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.principal.dallamada.fragments.ChatFragment;
import com.principal.dallamada.fragments.EventsFragment;
import com.principal.dallamada.fragments.NewsFragment;
import com.principal.dallamada.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    BottomNavigationView navigationView;

    ProfileFragment profileFragment;
    ChatFragment chatFragment;
    EventsFragment eventsFragment;
    NewsFragment newsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        profileFragment = new ProfileFragment();
        eventsFragment = new EventsFragment();
        chatFragment = new ChatFragment();
        newsFragment = new NewsFragment();

        //pre-render a fragmente
        loadFragment(profileFragment);

        navigationView = findViewById(R.id.bottomNavigationView);

        navigationView.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.fragmentProfile:
                        Log.w(TAG, "profile");
                        loadFragment(profileFragment);
                        break;
                    case R.id.fragmentEvents:
                        Log.w(TAG, "events");
                        loadFragment(eventsFragment);
                        break;
                    case R.id.fragmentChat:
                        Log.w(TAG, "chat");
                        loadFragment(chatFragment);
                        break;
                    case R.id.fragmentNews:
                        Log.w(TAG, "news");
                        loadFragment(newsFragment);
                        break;
                }
                return true;
            }
        } );

    }

    public void loadFragment (Fragment fragment){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout,fragment);
        transaction.commit();
    }
}