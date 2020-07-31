package com.athishworks.ccc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.athishworks.ccc.R;
import com.athishworks.ccc.pojomodels.HospitalDetails;

import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ViewHolder> {

    List<HospitalDetails> values;
    Context context;

    public HospitalAdapter(List<HospitalDetails> myDataset, Context context) {
        values = myDataset;
        this.context = context;
    }


    private OnItenClickListener listener;

    public interface OnItenClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setListener(OnItenClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public HospitalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.hospital_lists, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull HospitalAdapter.ViewHolder holder, int position) {
        HospitalDetails curHospital = values.get(position);
        holder.name.setText(curHospital.getName());
        holder.address.setText(curHospital.getAddress());

        String a = curHospital.getAvailableBeds() + "/" + curHospital.getTotalBeds();
        holder.bedCount.setText(a);

        a = "Lat : " + curHospital.getLatitude() + "\nLong : " + curHospital.getLongitude();
        holder.location.setText(a);
    }


    @Override
    public int getItemCount() {
        if (values==null)
            return 0;
        return values.size();
    }


    public void updateList(List<HospitalDetails> list){
        values = list;
        notifyDataSetChanged();
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name, address, bedCount, location;
        public ImageView editButton, deleteButton;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            address = v.findViewById(R.id.address);
            bedCount = v.findViewById(R.id.bed_count);
            location = v.findViewById(R.id.location);

            editButton = v.findViewById(R.id.edit_button);
            deleteButton = v.findViewById(R.id.delete_button);


            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null) {
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION) {
                            listener.onEditClick(position);
                        }
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null) {
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }

    }

}
