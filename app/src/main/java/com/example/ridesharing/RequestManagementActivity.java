package com.example.ridesharing;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RequestManagementAdapter requestManagementAdapter;
    private List<Request> requestManagementList; // List to hold requests for the current driver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_management);

        recyclerView = findViewById(R.id.recycler_view_requests_management);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        requestManagementList = new ArrayList<>();
        fetchDriverRequests();
    }

    private void fetchDriverRequests() {
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("RideSharing/Requests");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.e(TAG, "User not logged in.");
            return;
        }

        String userId = currentUser.getUid(); // Get current user’s ID

        requestsRef.orderByChild("driverID").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        requestManagementList.clear();

                        for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                            Request request = requestSnapshot.getValue(Request.class);
                            if (request != null) {
                                requestManagementList.add(request);
                            }
                        }

                        if (requestManagementAdapter == null) {
                            requestManagementAdapter = new RequestManagementAdapter(requestManagementList);
                            recyclerView.setAdapter(requestManagementAdapter);
                        } else {
                            requestManagementAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Database error: " + databaseError.getMessage());
                    }
                });
    }
}