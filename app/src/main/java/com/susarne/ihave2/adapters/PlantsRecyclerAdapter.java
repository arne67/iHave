package com.susarne.ihave2.adapters;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.susarne.ihave2.R;
import com.susarne.ihave2.models.PlantWithLists;
import com.susarne.ihave2.util.CurrentUser;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PlantsRecyclerAdapter extends RecyclerView.Adapter<PlantsRecyclerAdapter.ViewHolder> {
    private final ArrayList<PlantWithLists> mPlants;
    private final OnPlantListener mOnPlantListener;
    private Context mContext;
    private static final String APP_TAG = "MyCustomApp";
    private static final String TAG = "PlantsRecyclerAdapter";
    String recentlyTime;

    public PlantsRecyclerAdapter(ArrayList<PlantWithLists> plants, OnPlantListener onPlantListener) {
        this.mPlants = plants;
        this.mOnPlantListener = onPlantListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_plant_list_item, parent, false);

        LocalDateTime recentlyTimestamp = LocalDateTime.now().minusDays(5);
        ZonedDateTime recentlyTimestampDk = recentlyTimestamp.atZone(ZoneId.of("Europe/Copenhagen"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        recentlyTime = recentlyTimestampDk.format(formatter);
        Log.d(TAG, "onBindViewHolder: recentlyTime: " + recentlyTime);

        return new ViewHolder(view, mOnPlantListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            //#nytfelt-liste
            //holder.title.setText(mPlants.get(position).plant.getTitle() + "/" + mPlants.get(position).plant.getCreatedTime());
            holder.title.setText(mPlants.get(position).plant.getTitle());
            mContext = holder.itemView.getContext();
            Log.d(TAG, "onBindViewHolder: title+mainphotoname "+mPlants.get(position).plant.getTitle() + "/" + mPlants.get(position).plant.getMainPhotoName());
            if (mPlants.get(position).plant.getMainPhotoName() != null) {
                holder.mainPhoto.setVisibility(View.VISIBLE);
                File mediaStorageDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
                File imageFile = new File(mediaStorageDir.getPath() + File.separator + mPlants.get(position).plant.getMainPhotoName());

                Log.d(TAG, "onBindViewHolder: kalder glide - title+mainphotoname "+mPlants.get(position).plant.getTitle() + "/" + mPlants.get(position).plant.getMainPhotoName());
                Glide.with(mContext)
                        .load(imageFile.getAbsolutePath())
                        .into(holder.mainPhoto);
            } else{
                holder.mainPhoto.setVisibility(View.INVISIBLE);
            }
            if (mPlants.get(position).plant.getCreatedBy().equals(CurrentUser.getUserId())) {
                holder.communityMark.setVisibility(View.GONE);
                holder.newMark.setVisibility(View.GONE);
            } else {
                holder.communityMark.setVisibility(View.VISIBLE);
                if (mPlants.get(position).plant.getCreatedTime().compareTo(recentlyTime) > 0) {
                    holder.newMark.setVisibility(View.VISIBLE);
                } else {
                    holder.newMark.setVisibility(View.GONE);
                }
            }

            //Log.d(TAG, "onBindViewHolder: mPlants.get(position).plant.getCreatedTime(): " + mPlants.get(position).plant.getCreatedTime());

//            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//            holder.mainPhoto.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
        }
    }

    @Override
    public int getItemCount() {

        return mPlants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //nytfelt-liste
        TextView title;
        ImageView communityMark, newMark;

        ImageView mainPhoto;
        OnPlantListener onPlantListener;


        public ViewHolder(@NonNull View itemView, OnPlantListener onPlantListener) {
            super(itemView);
            //nytfelt-liste
            title = itemView.findViewById(R.id.plant_title);
            mainPhoto = itemView.findViewById(R.id.plant_main_photo);
            communityMark = itemView.findViewById(R.id.community_mark);
            newMark = itemView.findViewById(R.id.new_mark);

            this.onPlantListener = onPlantListener;
            itemView.setOnClickListener(this);
            //asfdasdf


        }

        @Override
        public void onClick(View view) {
            onPlantListener.onPlantClick(getAdapterPosition());

        }
    }

    public interface OnPlantListener {
        void onPlantClick(int position);
    }


}
