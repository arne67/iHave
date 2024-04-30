package com.susarne.ihave2.viewmodels;

import static com.susarne.ihave2.util.Constants.*;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.susarne.ihave2.models.PlantFlowerMonth;
import com.susarne.ihave2.models.PlantPhoto;
import com.susarne.ihave2.models.PlantWithLists;
import com.susarne.ihave2.persistence.PlantRepository;
import com.susarne.ihave2.util.ContextSingleton;
import com.susarne.ihave2.workers.SavePlantWorker;


public class PlantEditActivityViewModel extends ViewModel {

    private static final String TAG = "PlantEditActivityViewModel";

    private PlantRepository mPlantRepository;
    private boolean isNewPlant;
    private PlantWithLists editedPlant;
    private PlantWithLists savedPlant;
    private WorkManager mWorkManager;


    private final MutableLiveData<Integer> plantId = new MutableLiveData();
    public final LiveData<PlantWithLists> plant =
            Transformations.switchMap(plantId, (plantId) -> {
                return mPlantRepository.retrievePlantById(plantId);
            });



    public void initiate(Application application) {
        Context context;
        mWorkManager = WorkManager.getInstance(application);
        context= ContextSingleton.getContekst();
        mPlantRepository = new PlantRepository(context);
    }

    public void setPlantId(int plantId) {
        this.plantId.setValue(plantId);
    }

    public boolean isNewPlant() {
        return isNewPlant;
    }

    public void setNewPlant(boolean newPlant) {
        isNewPlant = newPlant;
    }

    public PlantWithLists getEditedPlant() {
        if (editedPlant!=null){
            Log.d(TAG, "getEditedPlant: "+editedPlant.plant.toString());
        }
        return editedPlant;
    }

    public void setEditedPlant(PlantWithLists editedPlant) {
        Log.d(TAG, "setEditedPlant: bb2-1 a "+editedPlant.toString());
        this.editedPlant = new PlantWithLists(editedPlant);
        Log.d(TAG, "setEditedPlant: bb2-1 b"+this.editedPlant.toString());
    }



    public boolean plantWithListsIsChanged(){
        Log.d(TAG, "isPlantChanged:editedPlant "+editedPlant.toString());
        Log.d(TAG, "isPlantChanged:savedPlant  "+savedPlant.toString());

        if (savedPlant==null) return true;
        if (editedPlant.hashCode()!=savedPlant.hashCode())
            return true;
        else
            if (editedPlant.equals(savedPlant))
                return false;
            else
                return true;
    }


    public boolean plantIsChanged(){
        //Log.d(TAG, "isPlantChanged: "+editedPlant.toString());
        //Log.d(TAG, "isPlantChanged: "+savedPlant.toString());

        if (savedPlant.plant==null) return true;
        if (editedPlant.plant.hashCode()!=savedPlant.plant.hashCode())
            return true;
        else
        if (editedPlant.plant.equals(savedPlant.plant))
            return false;
        else
            return true;
    }

    public boolean plantPhotoIsChanged(PlantPhoto savedPlantPhoto, PlantPhoto editedPlantPhoto){
        //Log.d(TAG, "isPlantChanged: "+editedPlant.toString());
        //Log.d(TAG, "isPlantChanged: "+savedPlant.toString());

        if (savedPlantPhoto==null) return true;
        if (editedPlantPhoto.hashCode()!=savedPlantPhoto.hashCode())
            return true;
        else
        if (editedPlantPhoto.equals(savedPlantPhoto))
            return false;
        else
            return true;
    }

    public boolean plantFlowerMonthIsChanged(PlantFlowerMonth savedPlantFlowerMonth, PlantFlowerMonth editedPlantFlowerMonth){
        //Log.d(TAG, "isPlantChanged: "+editedPlant.toString());
        //Log.d(TAG, "isPlantChanged: "+savedPlant.toString());

        if (savedPlantFlowerMonth==null) return true;
        if (editedPlantFlowerMonth.hashCode()!=savedPlantFlowerMonth.hashCode())
            return true;
        else
        if (editedPlantFlowerMonth.equals(savedPlantFlowerMonth))
            return false;
        else
            return true;
    }



    public void setSavedPlant(PlantWithLists savedPlant) {
        this.savedPlant = new PlantWithLists(savedPlant);
    }

    public void saveEditedPlant(){
        if (isNewPlant()) {
            saveNewPlant();
        } else {
            if (plantWithListsIsChanged())
                updatePlantWithLists();
        }
    }

    private void saveNewPlant() {
        Log.d(TAG, "saveNewPlant: ");
        editedPlant.plant.setCreatedInCloud(false);
        editedPlant.plant.setSyncedWithCloud(false);
        for (PlantPhoto plantPhoto:editedPlant.plantPhotos) {
            plantPhoto.setCreatedInCloud(false);
            plantPhoto.setSyncedWithCloud(false);
        }
        mPlantRepository.insertPlantWithLists(editedPlant);
        // Add WorkRequest to Cleanup temporary images
        Data data= new Data.Builder().putInt(WORKER_PLANT_ID,editedPlant.plant.getPlantId()).build();
        OneTimeWorkRequest savePlantRequest =
                new OneTimeWorkRequest.Builder(SavePlantWorker.class)
                        .setInputData(data)
                        .build();
        mWorkManager.enqueue(savePlantRequest);
    }

    private void updatePlantWithLists() {
        Log.d(TAG, "updatePlantWithLists: 1");
        updatePlant();
        updatePlantPhotos();
        updatePlantFlowerMonths();

        // Add WorkRequest to Cleanup temporary images
        Data data= new Data.Builder().putInt(WORKER_PLANT_ID,editedPlant.plant.getPlantId()).build();
        OneTimeWorkRequest savePlantRequest =
                new OneTimeWorkRequest.Builder(SavePlantWorker.class)
                        .setInputData(data)
                        .build();
        mWorkManager.enqueue(savePlantRequest);
    }


    private void updatePlant() {
        if (plantIsChanged()) {
            editedPlant.plant.setSyncedWithCloud(false);
            mPlantRepository.updatePlant(editedPlant.plant);
        }
    }

    private void updatePlantFlowerMonths() {
        // merge på MonthId
        // hvis tilføjet så inserter vi og hvis slettet så opdaterer vi sletmarkering

        editedPlant.plantFlowerMonths.sort((o1, o2)
                -> o1.getPlantFlowerMonthId() - o2.getPlantFlowerMonthId());
        savedPlant.plantFlowerMonths.sort((o1, o2)
                -> o1.getPlantFlowerMonthId() - o2.getPlantFlowerMonthId());
        int editedIdx = 0;
        int savedIdx = 0;
        // her løber vi plantFlowerMonths igennem og afgør om der er rettet eller tilføjet nogen
        Log.d(TAG, "updatePlant: size "+editedPlant.plantFlowerMonths.size()+' '+savedPlant.plantFlowerMonths.size());
        while (editedIdx+1 <= editedPlant.plantFlowerMonths.size()) {
            //Log.d(TAG, "updatePlantx: "+editedIdx+' '+editedPlant.plantFlowerMonths.get(editedIdx).getPlantFlowerMonthId()+' '+savedIdx+' '+savedPlant.plantFlowerMonths.get(savedIdx).getPlantFlowerMonthId());
            Log.d(TAG, "updatePlantx: "+editedIdx+' '+savedIdx);
            if (editedIdx+1 > editedPlant.plantFlowerMonths.size()) {
                // Vi har et saved monthId som ikke er med i editedplant. Det må ikke kunne forekomme
                // da vi aldrig sletter, men kun sætter en delete markering

                continue;
            }
            if (savedIdx+1 > savedPlant.plantFlowerMonths.size()){
                // Vi har et tilføjet en ny måned
                editedPlant.plantFlowerMonths.get(editedIdx).setCreatedInCloud(false);
                editedPlant.plantFlowerMonths.get(editedIdx).setSyncedWithCloud(false);
                mPlantRepository.insertPlantFlowerMonth(editedPlant.plantFlowerMonths.get(editedIdx));
                editedIdx++;
                continue;
            }
            if (editedPlant.plantFlowerMonths.get(editedIdx).getPlantFlowerMonthId()>savedPlant.plantFlowerMonths.get(savedIdx).getPlantFlowerMonthId()){
                // Vi har et saved monthid som ikke er med i editedplant. Det må ikke kunne forekomme
                // da vi aldrig sletter, men kun sætter en delete markering
                savedIdx++;
                continue;
            }
            if (editedPlant.plantFlowerMonths.get(editedIdx).getPlantFlowerMonthId()<savedPlant.plantFlowerMonths.get(savedIdx).getPlantFlowerMonthId()){
                // Vi har et tilføjet en ny måned
                editedPlant.plantFlowerMonths.get(editedIdx).setCreatedInCloud(false);
                editedPlant.plantFlowerMonths.get(editedIdx).setSyncedWithCloud(false);
                mPlantRepository.insertPlantFlowerMonth(editedPlant.plantFlowerMonths.get(editedIdx));
                editedIdx++;
                continue;
            }
            if (editedPlant.plantFlowerMonths.get(editedIdx).getPlantFlowerMonthId()==savedPlant.plantFlowerMonths.get(savedIdx).getPlantFlowerMonthId()) {
                // Vi har et ændret plantflowermonth (det vil sige deleted er skiftet mellem true og false)
                if (plantFlowerMonthIsChanged(savedPlant.plantFlowerMonths.get(savedIdx),editedPlant.plantFlowerMonths.get(editedIdx))){
                    editedPlant.plantFlowerMonths.get(editedIdx).setSyncedWithCloud(false);
                    mPlantRepository.updatePlantFlowerMonth(editedPlant.plantFlowerMonths.get(editedIdx));

                }
                savedIdx++;
                editedIdx++;
                continue;
            }

        }
    }
    private void updatePlantPhotos() {
        editedPlant.plantPhotos.sort((o1, o2)
                -> o1.getPhotoId() - o2.getPhotoId());
        savedPlant.plantPhotos.sort((o1, o2)
                -> o1.getPhotoId() - o2.getPhotoId());
        int editedIdx = 0;
        int savedIdx = 0;
        // her løber vi plantphotos igennem og afgør om der er rettet eller tilføjet nogen
        Log.d(TAG, "updatePlant: size "+editedPlant.plantPhotos.size()+' '+savedPlant.plantPhotos.size());
        while (editedIdx+1 <= editedPlant.plantPhotos.size()) {
            //Log.d(TAG, "updatePlantx: "+editedIdx+' '+editedPlant.plantPhotos.get(editedIdx).getPhotoId()+' '+savedIdx+' '+savedPlant.plantPhotos.get(savedIdx).getPhotoId());
            Log.d(TAG, "updatePlantx: "+editedIdx+' '+savedIdx);
            if (editedIdx+1 > editedPlant.plantPhotos.size()) {
                // Vi har et saved photo som ikke er med i editedplant. Det må ikke kunne forekomme
                // da vi aldrig sletter, men kun sætter en delete markering
                continue;
            }
            if (savedIdx+1 > savedPlant.plantPhotos.size()){
                // Vi har et tilføjet et nyt foto
                editedPlant.plantPhotos.get(editedIdx).setCreatedInCloud(false);
                editedPlant.plantPhotos.get(editedIdx).setSyncedWithCloud(false);
                mPlantRepository.insertPlantPhoto(editedPlant.plantPhotos.get(editedIdx));
                editedIdx++;
                continue;
            }
            if (editedPlant.plantPhotos.get(editedIdx).getPhotoId()>savedPlant.plantPhotos.get(savedIdx).getPhotoId()){
                // Vi har et saved photo som ikke er med i editedplant. Det må ikke kunne forekomme
                // da vi aldrig sletter, men kun sætter en delete markering
                savedIdx++;
                continue;
            }
            if (editedPlant.plantPhotos.get(editedIdx).getPhotoId()<savedPlant.plantPhotos.get(savedIdx).getPhotoId()){
                // Vi har et tilføjet et nyt foto
                editedPlant.plantPhotos.get(editedIdx).setCreatedInCloud(false);
                editedPlant.plantPhotos.get(editedIdx).setSyncedWithCloud(false);
                mPlantRepository.insertPlantPhoto(editedPlant.plantPhotos.get(editedIdx));
                editedIdx++;
                continue;
            }
            if (editedPlant.plantPhotos.get(editedIdx).getPhotoId()==savedPlant.plantPhotos.get(savedIdx).getPhotoId()) {
                // Vi har et ændret et foto
                if (plantPhotoIsChanged(savedPlant.plantPhotos.get(savedIdx),editedPlant.plantPhotos.get(editedIdx))){
                    editedPlant.plantPhotos.get(editedIdx).setSyncedWithCloud(false);
                    //mPlantRepository.updatePlantPhotoSync(editedPlant.plantPhotos.get(editedIdx));
                    mPlantRepository.updatePlantPhoto(editedPlant.plantPhotos.get(editedIdx));

                }
                savedIdx++;
                editedIdx++;
                continue;
            }

        }


    }


    public LiveData<Integer> getMaxPlantId(){
        return mPlantRepository.getMaxPlantId();
    }

    public PlantWithLists getSavedPlant() {
        return savedPlant;

    }


//    class SortbyPhotoId implements Comparator<PlantPhoto>
//    {
//        // Used for sorting in ascending order of
//        // roll number
//        public int compare(PlantPhoto a, PlantPhoto b)
//        {
//            return a.getPhotoId() - b.getPhotoId();
//        }
//    }


}
