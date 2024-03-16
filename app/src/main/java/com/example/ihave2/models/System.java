package com.example.ihave2.models;

import androidx.room.Entity;

@Entity(tableName = "System",primaryKeys = {"systemId"})
public class System {

//#nytfelt

    private int systemId;

    private int userId;
    private int lastUsedId;


    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        systemId = systemId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLastUsedId() {
        return lastUsedId;
    }

    public void setLastUsedId(int lastUsedId) {
        this.lastUsedId = lastUsedId;
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
