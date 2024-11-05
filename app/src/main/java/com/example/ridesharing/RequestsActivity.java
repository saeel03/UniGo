package com.example.ridesharing;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RequestsAdapter requestsAdapter;
    private List<Request> requestList; // This will hold all pending requests

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notification"); // Set toolbar title if needed
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_IN);
        }

        recyclerView = findViewById(R.id.recycler_view_requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize request list
        requestList = new ArrayList<>();

        // Fetch requests from database
        fetchPendingRequests();
    }

    private void fetchPendingRequests() {
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("RideSharing/Requests");

        // Get current user's ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("RequestsActivity", "No user is logged in.");
            return; // Handle the case when the user is not logged in
        }

        String userId = currentUser.getUid(); // Get the logged-in user's ID

        // Fetch requests where the clientID matches the logged-in user
        requestsRef.orderByChild("status").equalTo("pending").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requestList.clear(); // Clear the list to avoid duplicates

                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    Request request = requestSnapshot.getValue(Request.class);
                    Log.d("RequestsActivity", "Fetched Request: " + request);

                    if (request != null) {
                        // Check if the clientID matches the current user's ID
                        if (request.getClientID() != null && request.getClientID().equals(userId)) {
                            // Log to check for null values
                            Log.d("RequestsActivity", "Driver ID: " + request.getDriverID() + ", Ride ID: " + request.getRideID());
                            requestList.add(request); // Add to the list only if it matches
                        }
                    }
                }

                // Initialize adapter only if it's null
                if (requestsAdapter == null) {
                    requestsAdapter = new RequestsAdapter(requestList);
                    recyclerView.setAdapter(requestsAdapter);
                } else {
                    // If adapter already exists, notify changes
                    requestsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RequestsActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }
}