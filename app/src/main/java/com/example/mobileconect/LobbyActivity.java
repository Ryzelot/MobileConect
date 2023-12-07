package com.example.mobileconect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.example.mobileconect.adapter.UserAdapter;
import com.example.mobileconect.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class LobbyActivity extends AppCompatActivity {

    Button btnexit,btnaddcontact,mqtt;
    RecyclerView mRecycler;
    UserAdapter mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    SearchView buscar;

    public LobbyActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.recyclerContact);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        Query query = mFirestore.collection("contact");

        FirestoreRecyclerOptions<User> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();


        mAdapter = new UserAdapter(firestoreRecyclerOptions, this);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);

        mAuth = FirebaseAuth.getInstance();

        buscar = findViewById(R.id.buscarContacto);
        btnexit = findViewById(R.id.buttonLogout);
        btnaddcontact = findViewById(R.id.buttonAddContact);
        mqtt = findViewById(R.id.buttonMQTT);

        mqtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LobbyActivity.this, MQTTActivity.class);
                startActivity(intent);
            }
        });

        btnaddcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LobbyActivity.this, AddContactActivity.class);
                startActivity(intent);
            }
        });

        btnexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(LobbyActivity.this,MainActivity.class));
            }
        });

        searchContact();

    }

    private void searchContact() {
        buscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                textSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                textSearch(s);
                return false;
            }
        });

    }

    private void textSearch(String s) {
        Query query = mFirestore.collection("contact");
        FirestoreRecyclerOptions<User> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<User>().setQuery(query.orderBy("name").startAt(s).endAt(s+"~"),User.class).build();
        mAdapter = new UserAdapter(firestoreRecyclerOptions,this);
        mAdapter.startListening();
        mRecycler.setAdapter(mAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.startListening();
    }
}