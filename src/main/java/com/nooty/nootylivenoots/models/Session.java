package com.nooty.nootylivenoots.models;

public class Session {
    public String userId;
    public String principalId;

    public Session(String userId, String principalId) {
        this.userId = userId;
        this.principalId = principalId;
    }
}
