package com.example.ridesharing;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RequestManagementAdapter extends RecyclerView.Adapter<RequestManagementAdapter.RequestViewHolder> {
    private final List<Request> requestManagementList;

    public RequestManagementAdapter(List<Request> requestManagementList) {
        this.requestManagementList = requestManagementList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestManagementList.get(position);

        if (request == null) {
            holder.setDefaultValues();
            return;
        }

        // Display ClientID and ClientPhone
        holder.textViewName.setText(request.getClientName() != null ? request.getClientName() : "No ID");
        holder.textViewClientPhone.setText(request.getClientPhone() != null ? request.getClientPhone() : "No phone");

        // Display request status and description
        //holder.textViewStatus.setText(request.getStatus() != null ? request.getStatus() : "Status unknown");
        holder.textViewDescription.setText(request.getDescription() != null ? request.getDescription() : "Description unknown");

        // Set button actions for accept and reject
        holder.btn_accept.setOnClickListener(v -> handleAccept(request, holder));
        holder.btn_reject.setOnClickListener(v -> handleReject(request, holder));
    }

    @Override
    public int getItemCount() {
        return requestManagementList.size();
    }

    private void handleAccept(Request request, RequestViewHolder holder) {
        request.setStatus("Accepted");
    }

    private void handleReject(Request request, RequestViewHolder holder) {
        request.setStatus("Rejected");;
    }




    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewClientPhone, textViewStartLocation, textViewEndLocation, textViewDate, textViewTime, textViewDescription;
        Button btn_accept, btn_reject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewPassengerName);
            textViewClientPhone = itemView.findViewById(R.id.textViewClientPhone);
            textViewStartLocation = itemView.findViewById(R.id.textViewStartLocation);
            textViewEndLocation = itemView.findViewById(R.id.textViewEndLocation);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);

            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_reject = itemView.findViewById(R.id.rejectBtn);
        }

        public void setDefaultValues() {
        }
    }

}