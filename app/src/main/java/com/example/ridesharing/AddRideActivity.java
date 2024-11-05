package com.example.ridesharing;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddRideActivity extends AppCompatActivity {
    private static final String TAG = "AddRideActivity";

    private EditText phoneEditText, seatsEditText, timeEditText, dateEditText, vehicleTypeEditText, vehicleNumberEditText,editTextCost;
    private Button postButton;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ride);

        initializeViews();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the user is not authenticated
            return;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("RideSharing");

        loadUserDetails();
        setupDateAndTimePickers();
        postButton.setOnClickListener(v -> showPostRideDialog());
    }

    private void initializeViews() {
        phoneEditText = findViewById(R.id.editTextPhone);
        seatsEditText = findViewById(R.id.editTextSeats);
        timeEditText = findViewById(R.id.editTextTime);
        dateEditText = findViewById(R.id.editTextDate);
        vehicleTypeEditText = findViewById(R.id.editTextVehicleType);
        vehicleNumberEditText = findViewById(R.id.editTextVehicleNumber);
        editTextCost = findViewById(R.id.editTextCost);
        postButton = findViewById(R.id.buttonPost);
    }

    private void loadUserDetails() {
        loadData("phone", phoneEditText);
        loadData("vehicle_details/vehicle_type", vehicleTypeEditText);
        loadData("vehicle_details/vehicle_number", vehicleNumberEditText);
    }

    private void loadData(String childPath, EditText editText) {
        databaseReference.child("Users").child(userId).child(childPath)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String value = snapshot.getValue(String.class);
                        editText.setText(value != null ? value : "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to fetch data for " + childPath, error.toException());
                        Toast.makeText(AddRideActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupDateAndTimePickers() {
        dateEditText.setOnClickListener(v -> showDatePicker());
        timeEditText.setOnClickListener(v -> showTimePicker());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    dateEditText.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePicker = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    timeEditText.setText(timeFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePicker.show();
    }

    private void showPostRideDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_post_ride);

        EditText startingLocationEditText = dialog.findViewById(R.id.editTextStartingLocation);
        EditText endingLocationEditText = dialog.findViewById(R.id.editTextEndingLocation);
        Button postRideButton = dialog.findViewById(R.id.buttonPostRide);

        postRideButton.setOnClickListener(v -> {
            String startingLocation = startingLocationEditText.getText().toString().trim();
            String endingLocation = endingLocationEditText.getText().toString().trim();
            postRide(startingLocation, endingLocation);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void postRide(String startingLocation, String endingLocation) {
        String phone = phoneEditText.getText().toString().trim();
        String seats = seatsEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String vehicleType = vehicleTypeEditText.getText().toString().trim();
        String vehicleNumber = vehicleNumberEditText.getText().toString().trim();
        String cost = editTextCost.getText().toString().trim(); // Retrieve cost

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(seats) || TextUtils.isEmpty(time) ||
                TextUtils.isEmpty(date) || TextUtils.isEmpty(startingLocation) || TextUtils.isEmpty(endingLocation) ||
                TextUtils.isEmpty(cost)) { // Check if cost is filled
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isDigitsOnly(seats)) {
            Toast.makeText(this, "Seats must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DatabaseReference postRideRef = databaseReference.child("postRides").push();
            String rideId = postRideRef.getKey();
            Ride ride = new Ride(rideId, userId, phone, vehicleType, seats, time, date, vehicleNumber,
                    startingLocation, endingLocation, cost); // Pass cost to Ride object

            postRideRef.setValue(ride).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Ride posted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "Failed to post ride", task.getException());
                    Toast.makeText(this, "Failed to post ride", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error posting ride: " + e.getMessage());
            Toast.makeText(this, "Failed to post ride", Toast.LENGTH_SHORT).show();
        }
    }
}
