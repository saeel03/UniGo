package com.example.ridesharing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "HomeActivity";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private FloatingActionButton fabNotifications, fabAddRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "onCreate: HomeActivity started");

        // Initialize Firebase Auth and check for current user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d(TAG, "Current user ID: " + userId);
            checkUserRole(userId);
        } else {
            Log.e(TAG, "User not logged in, redirecting to login screen.");
            navigateToLogin();
            return;
        }

        fabNotifications = findViewById(R.id.fab_notifications);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fabAddRide = findViewById(R.id.fab_add_ride);
        fabAddRide.setOnClickListener(view -> navigateToAddRideActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            Log.d(TAG, "MapFragment found, initializing map.");
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "MapFragment is null.");
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);
    }

    private void navigateToRequestsActivity() {
        Log.d(TAG, "Navigating to RequestsActivity.");
        startActivity(new Intent(HomeActivity.this, RequestsActivity.class));
    }
    private void navigateToRequestManagementActivity() {
        Log.d(TAG, "Navigating to RequestsActivity.");
        startActivity(new Intent(HomeActivity.this, RequestManagementActivity.class));
    }
    private void checkUserRole(String userId) {
        Log.d(TAG, "Checking user role in Firebase for user ID: " + userId);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("RideSharing/Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.child("role").getValue(String.class);
                Log.d(TAG, "User role retrieved from Firebase: " + role);

                if ("Driver".equals(role)) {
                    fabAddRide.setVisibility(View.VISIBLE);
                    fabAddRide.setOnClickListener(view -> navigateToAddRideActivity());
                    fabNotifications.setOnClickListener(view -> navigateToRequestManagementActivity());
                    Log.d(TAG, "FAB is set to VISIBLE for Driver role");
                } else {
                    fabAddRide.setVisibility(View.GONE);
                    fabNotifications.setOnClickListener(view -> navigateToRequestsActivity());
                    Log.d(TAG, "FAB is set to GONE for non-Driver role");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read user role", error.toException());
            }
        });
    }


    private final BottomNavigationView.OnItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            Log.d(TAG, "BottomNav: Already in HomeActivity");
            startActivity(new Intent(HomeActivity.this, HomeActivity.class));
            return true;
        } else if (itemId == R.id.nav_book) {
            Log.d(TAG, "BottomNav: Navigating to BookActivity");
            startActivity(new Intent(HomeActivity.this, BookActivity.class));
            return true;
        } else if (itemId == R.id.nav_profile) {
            Log.d(TAG, "BottomNav: Navigating to ProfileActivity");
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            return true;
        } else {
            Log.e(TAG, "BottomNav: Unhandled item selected");
            return false;
        }
    };

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission granted, enabling MyLocation layer.");
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        } else {
            Log.d(TAG, "Requesting location permission.");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d(TAG, "Current location: " + currentLocation.toString());
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        } else {
                            Log.e(TAG, "getCurrentLocation: Location is null");
                        }
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "getCurrentLocation: Failed to fetch location", e);
                    });
        } else {
            Log.e(TAG, "getCurrentLocation: Permission not granted");
        }
    }

    private void navigateToLogin() {
        Log.d(TAG, "Navigating to LoginActivity.");
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish();
    }

    private void navigateToAddRideActivity() {
        Log.d(TAG, "Navigating to AddRideActivity.");
        startActivity(new Intent(HomeActivity.this, AddRideActivity.class));
    }
}