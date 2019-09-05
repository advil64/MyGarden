package com.example.mygarden;

import android.view.ViewGroup;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<Sensor> sensors;
    private Context context;
    final private String TAG = "Adapter";

    public Adapter(List<Sensor> sensors, Context context) {
        this.sensors = sensors;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_sensors, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Sensor sensor = sensors.get(position);
        holder.textViewHead.setText(sensor.getPosition());
        holder.textViewDesc.setText(sensor.getDesc());

        Picasso.with(context)
                .load(sensor.getPic())
                .resize(190,100)
                .into(holder.viewImage);


    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView viewImage;
        public TextView textViewHead;
        public TextView textViewDesc;

        public ViewHolder(View itemView) {
            super(itemView);

            viewImage = itemView.findViewById(R.id.viewImage);
            textViewHead = itemView.findViewById(R.id.textViewHead);
            textViewDesc = itemView.findViewById(R.id.textViewDesc);

        }
    }

}