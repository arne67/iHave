package com.example.ihave2.adapters;



import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ihave2.R;
import com.example.ihave2.models.PlantWithLists;

import java.io.File;
import java.util.ArrayList;

public class PlantsRecyclerAdapter extends RecyclerView.Adapter<PlantsRecyclerAdapter.ViewHolder> {
    private final ArrayList<PlantWithLists> mPlants;
    private final OnPlantListener mOnPlantListener;
    private Context mContext;
    private static final String APP_TAG = "MyCustomApp";
    private static final String TAG = "PlantsRecyclerAdapter";

    public PlantsRecyclerAdapter(ArrayList<PlantWithLists> plants, OnPlantListener onPlantListener) {
        this.mPlants = plants;
        this.mOnPlantListener=onPlantListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_plant_list_item, parent, false);
        return new ViewHolder(view,mOnPlantListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            //#nytfelt-liste
            holder.title.setText(mPlants.get(position).plant.getTitle());
            mContext = holder.itemView.getContext();
            File mediaStorageDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
            File imageFile = new File(mediaStorageDir.getPath() + File.separator + mPlants.get(position).plant.getMainPhotoName());
            Glide.with(mContext)
                    .load(imageFile.getAbsolutePath())
                    .into(holder.mainPhoto);
//            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//            holder.mainPhoto.setImageBitmap(bitmap);
        }catch (NullPointerException e){
        }
    }

    @Override
    public int getItemCount() {

        return mPlants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //nytfelt-liste
        TextView title;
        ImageView mainPhoto;
        OnPlantListener onPlantListener;


        public ViewHolder(@NonNull View itemView, OnPlantListener onPlantListener) {
            super(itemView);
            //nytfelt-liste
            title=itemView.findViewById(R.id.plant_title);
            mainPhoto=itemView.findViewById(R.id.plant_main_photo);

            this.onPlantListener = onPlantListener;
            itemView.setOnClickListener(this);
            //asfdasdf


        }

        @Override
        public void onClick(View view) {
            onPlantListener.onPlantClick(getAdapterPosition());

        }
    }
    public interface OnPlantListener{
        void onPlantClick(int position);
    }


}
