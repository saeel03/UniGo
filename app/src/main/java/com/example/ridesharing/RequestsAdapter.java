package com.example.ridesharing;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {
    private final List<Request> requestList;

    public RequestsAdapter(List<Request> requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);

        if (request != null) {
            // Fetch driver details
            String driverID = request.getDriverID();
            String rideID = request.getRideID();

            if (driverID == null || rideID == null) {
                Log.e("RequestsAdapter", "Driver ID or Ride ID is null at position: " + position);
                holder.textViewDriverName.setText("Unknown Driver");
                holder.textViewDriverPhone.setText("Unknown Phone");
                holder.textViewStartLocation.setText("Unknown Start");
                holder.textViewEndLocation.setText("Unknown End");
                holder.textViewDate.setText("Unknown Date");
                holder.textViewTime.setText("Unknown Time");
                holder.textViewDescription.setText(request.getDescription() != null ? request.getDescription() : "No description");
                holder.textViewStatus.setText(request.getStatus() != null ? request.getStatus() : "Status unknown");
                return;
            }

            // Create references to the User and Ride nodes
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("RideSharing/Users").child(driverID);
            DatabaseReference ridesRef = FirebaseDatabase.getInstance().getReference("RideSharing/postRides").child(rideID);

            // Fetch user details
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    User user = userSnapshot.getValue(User.class);

                    if (user != null) {
                        holder.textViewDriverName.setText(user.getName() != null ? user.getName() : "Unknown Driver");
                        holder.textViewDriverPhone.setText(user.getPhone() != null ? user.getPhone() : "Unknown Phone");
                    } else {
                        holder.textViewDriverName.setText("User not found");
                        holder.textViewDriverPhone.setText("Unknown Phone");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("RequestsAdapter", "Error fetching driver data: " + databaseError.getMessage());
                    holder.textViewDriverName.setText("Error fetching name");
                    holder.textViewDriverPhone.setText("Error fetching phone");
                }
            });

            // Fetch ride details
            ridesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot rideSnapshot) {
                    Ride ride = rideSnapshot.getValue(Ride.class);

                    if (ride != null) {
                        holder.textViewStartLocation.setText(ride.getStartingLocation() != null ? ride.getStartingLocation() : "Unknown Start");
                        holder.textViewEndLocation.setText(ride.getEndingLocation() != null ? ride.getEndingLocation() : "Unknown End");
                        holder.textViewDate.setText(ride.getDate() != null ? ride.getDate() : "Unknown Date");
                        holder.textViewTime.setText(ride.getTime() != null ? ride.getTime() : "Unknown Time");
                        holder.textViewDescription.setText(request.getDescription() != null ? request.getDescription() : "No description");
                        holder.textViewStatus.setText(request.getStatus() != null ? request.getStatus() : "Status unknown");
                    } else {
                        holder.textViewStartLocation.setText("Ride not found");
                        holder.textViewEndLocation.setText("Ride not found");
                        holder.textViewDate.setText("Unknown Date");
                        holder.textViewTime.setText("Unknown Time");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("RequestsAdapter", "Error fetching ride data: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("RequestsAdapter", "Request is null at position: " + position);
        }
    }


    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDriverName;
        TextView textViewStartLocation;
        TextView textViewEndLocation;
        TextView textViewDriverPhone;
        TextView textViewDate;
        TextView textViewTime;
        TextView textViewDescription;
        TextView textViewStatus;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDriverName = itemView.findViewById(R.id.textViewName);
            textViewStartLocation = itemView.findViewById(R.id.textViewStartLocation);
            textViewEndLocation = itemView.findViewById(R.id.textViewEndLocation);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewDriverPhone = itemView.findViewById(R.id.textViewPhone);
        }
    }
}