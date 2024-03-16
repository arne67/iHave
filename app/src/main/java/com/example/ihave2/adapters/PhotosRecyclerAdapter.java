package com.example.ihave2.adapters;



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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.ihave2.R;
import com.example.ihave2.models.PhotoListElement;

import java.io.File;
import java.util.ArrayList;

public class PhotosRecyclerAdapter extends RecyclerView.Adapter<PhotosRecyclerAdapter.ViewHolder> {
    private final ArrayList<PhotoListElement> mPhotos;
    private final OnPlantListener mOnPlantListener;
    private Context mContext;
    private static final String APP_TAG = "MyCustomApp";
    private static final String TAG = "PhotosRecyclerAdapter";

    public PhotosRecyclerAdapter(ArrayList<PhotoListElement> photos, OnPlantListener onPlantListener) {
        this.mPhotos = photos;
        this.mOnPlantListener=onPlantListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_photo_list_item, parent, false);
        return new ViewHolder(view,mOnPlantListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            //#nytfelt-liste
            holder.title.setText(mPhotos.get(position).getPlantPhoto().getPhotoName());
            mContext = holder.itemView.getContext();
            File mediaStorageDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
            File imageFile = new File(mediaStorageDir.getPath() + File.separator + mPhotos.get(position).getPlantPhoto().getPhotoName());
            Glide.with(mContext)
                    .load(imageFile.getAbsolutePath())
                    .transform(new CenterCrop())
                    .into(holder.mainPhoto);
//            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//            holder.mainPhoto.setImageBitmap(bitmap);
        }catch (NullPointerException e){
        }
    }

    @Override
    public int getItemCount() {

        return mPhotos.size();
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: vi har klikket - inline");
                    onPlantListener.onPlantClick(getAdapterPosition());

                }
            });

            //asfdasdf
        }


        @Override
        public void onClick(View view) {

        }
    }
    public interface OnPlantListener{
        void onPlantClick(int position);
        void onMainPhotoButtonClicked(int position);
    }


}
