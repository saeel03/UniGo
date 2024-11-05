package com.example.ridesharing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {
    private final List<Ride> rideList;
    private final Context context;

    public RideAdapter(List<Ride> rideList, Context context) {
        this.rideList = rideList;
        this.context = context;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);

        holder.seatsTextView.setText(ride.getSeats());
        holder.vehicleTypeTextView.setText(ride.getVehicleType());
        holder.startLocationTextView.setText(ride.getStartingLocation());
        holder.endLocationTextView.setText(ride.getEndingLocation());
        holder.dateTextView.setText(ride.getDate());
        holder.timeTextView.setText(ride.getTime());
        holder.costTextView.setText("Cost: " + ride.getCost()); // Set the cost text

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("RideSharing")
                .child("Users").child(ride.getUserId()).child("name");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                holder.nameTextView.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.nameTextView.setText("Name unavailable");
            }
        });

        holder.buttonBook.setOnClickListener(v -> {
            String rideId = ride.getRideId();
            Log.d("RideAdapter", "Ride ID: " + rideId); // Log the ride ID before passing it
            Intent intent = new Intent(context, SendRequestActivity.class);
            intent.putExtra("RIDE_ID", rideId);
            intent.putExtra("USER_ID", ride.getUserId());
            intent.putExtra("RIDE_DETAILS", "From: " + ride.getStartingLocation() +
                    "\nTo: " + ride.getEndingLocation() +
                    "\nSeats: " + ride.getSeats() +
                    "\nVehicle Type: " + ride.getVehicleType() +
                    "\nCost: " + ride.getCost()); // Include cost in ride details
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return rideList.size();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView seatsTextView, vehicleTypeTextView, startLocationTextView, endLocationTextView, nameTextView, dateTextView, timeTextView, costTextView;
        Button buttonBook;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            seatsTextView = itemView.findViewById(R.id.textViewSeats);
            vehicleTypeTextView = itemView.findViewById(R.id.textViewVehicleType);
            startLocationTextView = itemView.findViewById(R.id.textViewStartLocation);
            endLocationTextView = itemView.findViewById(R.id.textViewEndLocation);
            nameTextView = itemView.findViewById(R.id.textViewName);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            timeTextView = itemView.findViewById(R.id.textViewTime);
            costTextView = itemView.findViewById(R.id.textViewCost); // New cost TextView
            buttonBook = itemView.findViewById(R.id.buttonBook);
        }
    }

}
