package com.example.ridesharing;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SendRequestActivity extends AppCompatActivity {
    private static final String TAG = "SendRequestActivity";
    private EditText editTextDescription;
    private TextView textViewUserName, textViewUserPhone; // Declare TextView variables
    private String currentUserId;
    private String currentUserName;
    private String currentUserPhone;
    private String driverId, rideId; // Add variables for driver ID and ride ID
    private DatabaseReference dbRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);



        // Initialize UI elements
        textViewUserName = findViewById(R.id.textViewUserName);
        textViewUserPhone = findViewById(R.id.textViewUserPhone);
        editTextDescription = findViewById(R.id.editTextDescription);
        Button buttonSendRequest = findViewById(R.id.buttonSendRequest);

        currentUserId = getCurrentUserId();
        dbRef = FirebaseDatabase.getInstance().getReference("RideSharing/Users");

        // Retrieve the ride ID and driver ID from the intent
        rideId = getIntent().getStringExtra("RIDE_ID");
        driverId = getIntent().getStringExtra("USER_ID");
        Log.d(TAG, "Ride ID retrieved in SendRequestActivity: " + rideId);

        Log.d(TAG, "Driver ID retrieved: " + driverId);

        if (currentUserId != null) {
            getUserData(currentUserId);
        } else {
            Log.d(TAG, "No user is currently logged in");
        }

        // Retrieve ride details from intent
        String rideDetails = getIntent().getStringExtra("RIDE_DETAILS");
        Log.d(TAG, "Ride details retrieved from intent: " + rideDetails);

        // Update TextView with ride details
        TextView textViewRideDetails = findViewById(R.id.textViewRideDetails);
        textViewRideDetails.setText(rideDetails != null ? rideDetails : "No ride details available");

        buttonSendRequest.setOnClickListener(v -> {
            String description = editTextDescription.getText().toString().trim();
            if (description.isEmpty()) {
                Toast.makeText(SendRequestActivity.this, "Please enter a description", Toast.LENGTH_SHORT).show();
                return;
            }
            sendRequest(description, rideId); // Pass rideId to sendRequest
        });
    }

    private void sendRequest(String description, String rideId) {
        if (currentUserId == null) {
            Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User ID is missing, finishing activity.");
            finish();
            return;
        }

        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("RideSharing").child("Requests");
        String requestId = requestsRef.push().getKey();
        Log.d(TAG, "Generated request ID: " + requestId);

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("clientID", currentUserId);
        requestData.put("clientName", currentUserName);
        requestData.put("clientPhone", currentUserPhone);
        requestData.put("driverID", driverId); // Include the driver ID
        requestData.put("description", description);
        requestData.put("rideID", rideId); // Include the ride ID in the request data
        requestData.put("status", "pending");

        Log.d(TAG, "Sending request with data: " + requestData);

        if (requestId != null) {
            requestsRef.child(requestId).setValue(requestData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Request sent successfully with ID: " + requestId);
                        Toast.makeText(SendRequestActivity.this, "Request sent!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to send request", e);
                        Toast.makeText(SendRequestActivity.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e(TAG, "Failed to generate a request ID");
        }
    }

    private String getCurrentUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null); // Get user ID from SharedPreferences

        // Fallback to FirebaseAuth user ID if SharedPreferences is null
        if (userId == null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                userId = currentUser.getUid();
            }
        }
        Log.d(TAG, "Current user ID: " + userId);
        return userId;
    }

    private void getUserData(String userId) {
        Log.d(TAG, "Fetching user data for user ID: " + userId);
        dbRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "User data snapshot exists.");
                    populateUserData(dataSnapshot);
                } else {
                    Log.d(TAG, "User data snapshot does not exist.");
                    Toast.makeText(SendRequestActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user data: " + databaseError.getMessage());
                Toast.makeText(SendRequestActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void populateUserData(DataSnapshot dataSnapshot) {
        currentUserName = dataSnapshot.child("name").getValue(String.class);
        currentUserPhone = dataSnapshot.child("phone").getValue(String.class);

        Log.d(TAG, "User data populated: Name: " + currentUserName + ", Phone: " + currentUserPhone);

        // Update UI elements with user data
        textViewUserName.setText(currentUserName != null ? currentUserName : "N/A");
        textViewUserPhone.setText(currentUserPhone != null ? currentUserPhone : "N/A");
    }
}
