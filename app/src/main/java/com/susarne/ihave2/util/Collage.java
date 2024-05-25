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
        for (int i = 0; i < 4; i++) {
            collageView[i].setImageBitmap(null);
        }
        int collageViewPosition=0;
        int numberOfActivPhotos=0;
        for (int i = 0; i < photos.size(); i++) {
            if (!photos.get(i).isDeleted()){
                numberOfActivPhotos++;
                if (collageViewPosition<4){
                    mImageFile = new File(mMediaStorageDir.getPath() + File.separator + photos.get(i).getPhotoName());
                    //bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    //mMultiPhoto.setImageBitmap(bitmap);
                    Glide.with(context)
                            .load(mImageFile.getAbsolutePath())
                            .into(collageView[collageViewPosition]);

                }
                collageViewPosition++;
            }
            if (numberOfActivPhotos>4){
                String string1 = "+"+(numberOfActivPhotos-3)+"\n";
                String string2 = "billeder";
                SpannableString spannableString = new SpannableString(string1 + string2);

                // Anvend relativ tekststørrelse på string2 (halv størrelse)
                RelativeSizeSpan halfSizeSpan = new RelativeSizeSpan(0.5f);
                spannableString.setSpan(halfSizeSpan, string1.length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //String s= "+"+(photos.size()-3)+"\n billeder";
                photoCount.setText(spannableString);
                collageView[3].setImageBitmap(null);

            }


        }
    }

}
