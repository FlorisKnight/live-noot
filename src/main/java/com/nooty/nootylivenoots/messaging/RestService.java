package com.nooty.nootylivenoots.messaging;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nooty.nootylivenoots.models.Noot;
import com.nooty.nootylivenoots.models.User;
import com.nooty.nootylivenoots.models.viewmodels.NootInitViewModel;

import java.util.ArrayList;

public class RestService extends RestServiceBase {
    private final String url = "gateway-deployment:8080/";
    Gson gson = new Gson();

    public ArrayList<Noot> getAllNoots() {
        ArrayList<Noot> result = executeQueryGet(getQuery("noot/"), ArrayList.class);
        if (result != null) {
            String array = gson.toJson(result);
            ArrayList<Noot> list = gson.fromJson(array, new TypeToken<ArrayList<Noot>>(){}.getType());
            return list;
        } else
            return new ArrayList<Noot>();
    }

    public ArrayList<Noot> getNootsTimeline(String id, NootInitViewModel body) {
        ArrayList<Noot> result = executeQueryPost(body, getQuery("noot/timeline/"+id), ArrayList.class);
        if (result != null) {
            String array = gson.toJson(result);
            ArrayList<Noot> list = gson.fromJson(array, new TypeToken<ArrayList<Noot>>(){}.getType());
            return list;
        } else
            return new ArrayList<Noot>();
    }

    public ArrayList<String> getFollowersFromId(String id) {
        ArrayList<String> result = executeQueryGet(getQuery("follow/followers/"+ id), ArrayList.class);
        if (result != null) {
            String array = gson.toJson(result);
            ArrayList<String> list = gson.fromJson(array, new TypeToken<ArrayList<String>>(){}.getType());
            return list;
        } else
            return new ArrayList<String>();
    }

    public User getUserById(String id) {
        User result = executeQueryGet(getQuery("account/" + id), User.class);
        if (result != null) {
            return result;
        } else
            return new User();
    }

    private String getQuery(String path) {
        return url + path;
    }
}
