package com.susarne.ihave2.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.susarne.ihave2.api.PlantApiClient;
import com.susarne.ihave2.models.System;
import com.susarne.ihave2.models.Taxon;
import com.susarne.ihave2.models.TaxonDto;
import com.susarne.ihave2.models.GetUpdatedTaxonsDto;
import com.susarne.ihave2.persistence.PlantRepository;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class GetTaxonWorker extends Worker {
    private static final String TAG = "GetTaxonWorker";
    private static final String APP_TAG = "MyCustomApp";
    private boolean mSucces;
    private String mAccessTokenString;
    private Context mContext;

    private PlantRepository mPlantRepository;


    public GetTaxonWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mPlantRepository = new PlantRepository(context);

    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: getplantworker1");

        GetUpdatedTaxonsDto taxonsDto = getUpdatedTaxons(getLastGetUpdatedTaxonsUntil());
        if (taxonsDto.getTaxonDtos()!=null){
            for (TaxonDto taxonDto : taxonsDto.getTaxonDtos()) {
                Log.d(TAG, "doWork: Dansk navn" + taxonDto.getAccepteret_dansk_navn());
                Taxon taxon = new Taxon(taxonDto);
                mPlantRepository.upsertTaxon(taxon);
            }
        }
        mPlantRepository.setLastGetUpdatedTaxonsUntil(taxonsDto.getUpdatedUntil());

        return Result.success();
    }



    private String getLastGetUpdatedTaxonsUntil() {
        System system = mPlantRepository.retrieveSystemByIdSync(0);
        if (system == null) {
            return "1900-01-01 00:00:00.000000";

        } else {
            //return "1900-01-01 00:00:00.000000";
            return system.getLastGetUpdatedTaxonsUntil();
        }
    }

    private GetUpdatedTaxonsDto getUpdatedTaxons(String lastGetUpdatedTaxonsUntil) {
        //her skal vi kalde retrofit for at uploade synkront
        Call<GetUpdatedTaxonsDto> call = PlantApiClient.getInstance().getMyApi().getUpdatedTaxons(lastGetUpdatedTaxonsUntil);
        try {
            Response<GetUpdatedTaxonsDto> response = call.execute();
            if (response.isSuccessful()) {
                // handle successful response
                Log.d(TAG, "uploadPlant: succes ");
                return response.body();
            } else {
                // handle unsuccessful response
                Log.d(TAG, "uploadPlant: notfound" + response.errorBody().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "uploadPlant: io-fejl");
        }
        return null;

    }


}
