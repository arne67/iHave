package com.susarne.ihave2.util;

import android.content.Context;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.susarne.ihave2.models.PlantPhoto;

import java.io.File;
import java.util.List;

public class Collage {
    private static File mImageFile,mMediaStorageDir;
    private static final String APP_TAG = "MyCustomApp";

    public static void showCollage(List<PlantPhoto> photos, ImageView[] collageView, TextView photoCount, Context context) {
        mMediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (photos.size() > 0) {
            mImageFile = new File(mMediaStorageDir.getPath() + File.separator + photos.get(0).getPhotoName());
            //bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            //mMultiPhoto.setImageBitmap(bitmap);
            Glide.with(context)
                    .load(mImageFile.getAbsolutePath())
                    .into(collageView[0]);
        }
        if (photos.size() > 1) {
            mImageFile = new File(mMediaStorageDir.getPath() + File.separator + photos.get(1).getPhotoName());
            //bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            //mMultiPhoto.setImageBitmap(bitmap);
            Glide.with(context)
                    .load(mImageFile.getAbsolutePath())
                    .into(collageView[1]);
        }
        if (photos.size() > 2) {
            mImageFile = new File(mMediaStorageDir.getPath() + File.separator + photos.get(2).getPhotoName());
            //bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            //mMultiPhoto.setImageBitmap(bitmap);
            Glide.with(context)
                    .load(mImageFile.getAbsolutePath())
                    .into(collageView[2]);
        }
        if (photos.size()==4) {
            mImageFile = new File(mMediaStorageDir.getPath() + File.separator + photos.get(3).getPhotoName());
            //bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            //mMultiPhoto.setImageBitmap(bitmap);
            Glide.with(context)
                    .load(mImageFile.getAbsolutePath())
                    .into(collageView[3]);
        }
        if (photos.size()>4) {
            String string1 = "+"+(photos.size()-3)+"\n";
            String string2 = "billeder";
            SpannableString spannableString = new SpannableString(string1 + string2);

            // Anvend relativ tekststørrelse på string2 (halv størrelse)
            RelativeSizeSpan halfSizeSpan = new RelativeSizeSpan(0.5f);
            spannableString.setSpan(halfSizeSpan, string1.length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //String s= "+"+(photos.size()-3)+"\n billeder";
            photoCount.setText(spannableString);
            collageView[3].setImageBitmap(null);
            ;
        }
    }
}
