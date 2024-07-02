package com.susarne.ihave2.models;

import androidx.room.Entity;

@Entity(tableName = "System",primaryKeys = {"systemId"})
public class System {

//#nytfelt

    private int systemId;

    private String userId;
    private int lastUsedId;
    //Tidspunkt (fra cloud) for hvilket tidspunkt vi fået opdateringer hentet ned for andre brugere (til og med)
    private String lastGetUpdatedPlantsUntil;

    private String lastGetUpdatedTaxonsUntil;

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        systemId = systemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String   userId) {
        this.userId = userId;
    }

    public int getLastUsedId() {
        return lastUsedId;
    }

    public void setLastUsedId(int lastUsedId) {
        this.lastUsedId = lastUsedId;
    }

    public String getLastGetUpdatedPlantsUntil() {
        return lastGetUpdatedPlantsUntil;
    }

    public void setLastGetUpdatedPlantsUntil(String lastGetUpdatedPlantsUntil) {
        this.lastGetUpdatedPlantsUntil = lastGetUpdatedPlantsUntil;
    }

    public String getLastGetUpdatedTaxonsUntil() {
        return lastGetUpdatedTaxonsUntil;
    }

    public void setLastGetUpdatedTaxonsUntil(String lastGetUpdatedTaxonsUntil) {
        this.lastGetUpdatedTaxonsUntil = lastGetUpdatedTaxonsUntil;
    }

    public System() {
    }


    public String toString() {
        return "System{" +
                "systemId=" + systemId +
                "userId=" + userId +
                "LastUsedId" + lastUsedId +
                '}';
    }

}
