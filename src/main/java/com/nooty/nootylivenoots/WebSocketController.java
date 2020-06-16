package com.nooty.nootylivenoots;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nooty.nootylivenoots.messaging.NootSender;
import com.nooty.nootylivenoots.messaging.RestService;
import com.nooty.nootylivenoots.models.Session;
import com.nooty.nootylivenoots.models.User;
import com.nooty.nootylivenoots.models.viewmodels.NootInitViewModel;
import com.nooty.nootylivenoots.models.EncapsulatedMessage;
import com.nooty.nootylivenoots.models.Noot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class WebSocketController {
    private Gson gson;
    private final SimpMessagingTemplate template;
    private NootSender nootSender;
    private RestService restService;
    private static final String WS_MESSAGE_TRANSFER_DESTINATION = "/global";
    private ArrayList<Session> users = new ArrayList<>();

    @Autowired
    WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
        gson = new Gson();
        nootSender = new NootSender();
        restService = new RestService();
    }

    @MessageMapping("/post/noot")
    public void onReceivedMessage(String message, Principal principal) {
        System.out.println(message);
        System.out.println(principal.getName());
        messageHandler(message, principal.getName());
    }

    private void messageHandler(String message, String principalName) {
        EncapsulatedMessage msg = gson.fromJson(message, EncapsulatedMessage.class);
        switch (msg.messageType) {
            default:
                System.out.println("Unknown messageType: " + msg.messageType);
                break;
            case ("noot.post"):
                nootPost(gson.fromJson(msg.messageData, Noot.class));
                break;
            case ("noot.init"):
                nootInit(gson.fromJson(msg.messageData, NootInitViewModel.class), principalName);
                break;
            case ("noot.paginate"):
                break;
        }
    }

    private void nootPost(Noot noot){
        // Method makes new noot and sends this to message que and global timeline
        // gives id
        noot.setId(UUID.randomUUID().toString());
        String messageData = gson.toJson(noot);
        // Sends to message que
        try {
            nootSender.sendNootToNootService(messageData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        noot.setUser(restService.getUserById(noot.getUserId()));
        EncapsulatedMessage msg = new EncapsulatedMessage("noot.post", gson.toJson(noot));
        SendToFollowers(msg, noot.getUserId());
        template.convertAndSend("/global", msg);
    }

    private void nootInit(NootInitViewModel data, String principalName){
        // Method send initial page loading noots to certain user
        ArrayList<Noot> noots = new ArrayList<>();
        if (data.wantedNoots.equals("global")) {
            noots = restService.getAllNoots();
        } else {
            System.out.println(data);
            int i;
            for (i = 0; i < this.users.size(); i++) {
                if (this.users.get(i).userId.equals(data.userId)){
                    this.users.remove(i);
                    break;
                }
            }
            noots = restService.getNootsTimeline(data.userId, data);
            users.add(new Session(data.userId, principalName));
            // noots gets personal following list
        }

        noots = getUsersForNoots(noots);
        template.convertAndSendToUser(principalName,"/personal", new EncapsulatedMessage("noot.init", gson.toJson(noots)));
    }

    // Method sends new noot to followers
    private void SendToFollowers(EncapsulatedMessage msg, String sender) {
        ArrayList<String> followers = restService.getFollowersFromId(sender);
        for (Session u: users) {
            System.out.println(u.userId + "------" + u.principalId);
            for (String f: followers) {
                System.out.println(f);
                if (u.userId.equals(f)) {
                    template.convertAndSendToUser(u.principalId, "/personal", msg);
                }
            }
        }
    }

    private EncapsulatedMessage nootPaginate(){
        return null;
    }

    private ArrayList<Noot> getUsersForNoots(ArrayList<Noot> noots) {
        for (Noot noot: noots) {
            User u = restService.getUserById(noot.getUserId());
            if (u.getId() != null) {
                noot.setUser(u);
            }
        }
        return noots;
    }
}

