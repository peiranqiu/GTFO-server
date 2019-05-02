package project.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import project.models.Friend;
import project.models.User;
import project.repositories.FriendRepository;
import project.repositories.UserRepository;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class FriendService {

  String URL = "https://exp.host/--/api/v2/push/send";
  ObjectMapper mapper = new ObjectMapper();
  OkHttpClient client = new OkHttpClient();

  @Autowired
  FriendRepository friendRepository;
  @Autowired
  UserRepository userRepository;

  @PostMapping("/api/friend/request/{userId}")
  public void userSendRequest(@PathVariable("userId") int userId, @RequestBody User firstUser) throws IOException {
    Optional<User> data = userRepository.findById(userId);

    if (data.isPresent()) {
      User secondUser = data.get();

      Friend friend = new Friend();
      friend.setFirstUser(firstUser);
      friend.setSecondUser(secondUser);
      friend.setStatus(false);
      friendRepository.save(friend);

      String pushToken = secondUser.getPushToken();
      if(pushToken != null) {
        JSONObject obj = new JSONObject();
        obj.put("to", pushToken);
        obj.put("title", "You've got new friend!");
        obj.put("badge", 1);
        obj.put("body", firstUser.getUsername() + " sent you a friend request");
        MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON, obj.toString());
        Request request = new Request.Builder().url(URL)
                .post(requestBody)
                .addHeader("content-Type", "application/json")
                .addHeader("host", "exp.host")
                .addHeader("accept-encoding", "gzip, deflate")
                .addHeader("accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();
      }
    }
  }

  @GetMapping("/api/friend/send/{userId}")
  public List<User> findFriendSends(@PathVariable("userId") int userId) {
    List<Friend> friendHistory = (List<Friend>) friendRepository.findAll();
    List<User> users = new ArrayList<>();

    for (Friend friend : friendHistory) {
      if (friend.getFirstUser().getId() == userId && !friend.isStatus() && friend.getSecondUser().isStatus()) {
        users.add(friend.getSecondUser());
      }
    }
    return users;
  }

  @GetMapping("/api/friend/request/{userId}")
  public List<Friend> findFriendRequests(@PathVariable("userId") int userId) {
    List<Friend> friends = new ArrayList<Friend>();
    List<Friend> friendHistory = (List<Friend>) friendRepository.findAll();

    for (Friend friend : friendHistory) {
      if (friend.getSecondUser().getId() == userId && !friend.isStatus() && friend.getFirstUser().isStatus()) {
        friends.add(friend);
      }
    }
    return friends;
  }


  @PostMapping("/api/friend/{friendId}")
  public void userAcceptRequest(@PathVariable("friendId") int friendId) throws IOException {

    Optional<Friend> data = friendRepository.findById(friendId);
    if (data.isPresent()) {
      Friend friend = data.get();
      friend.setStatus(true);
      friendRepository.save(friend);

      String pushToken = friend.getFirstUser().getPushToken();
      if(pushToken != null) {
        JSONObject obj = new JSONObject();
        obj.put("to", pushToken);
        obj.put("title", "You've got new friend!");
        obj.put("body", friend.getSecondUser().getUsername() + " accepted your friend request");
        MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON, obj.toString());
        Request request = new Request.Builder().url(URL)
                .post(requestBody)
                .addHeader("content-Type", "application/json")
                .addHeader("host", "exp.host")
                .addHeader("accept-encoding", "gzip, deflate")
                .addHeader("accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();
      }
    }
  }

  @DeleteMapping("/api/friend/{friendId}")
  public void userDeleteRequest(@PathVariable("friendId") int friendId) {
    friendRepository.deleteById(friendId);
  }

  @DeleteMapping("/api/friend/{userId}/delete/{anotherUserId}")
  public void DeleteFriend(@PathVariable("userId") int userId, @PathVariable("anotherUserId") int anotherUserId) {
    List<Friend> friendHistory = (List<Friend>) friendRepository.findAll();
    for (Friend friend : friendHistory) {
      if ((friend.getFirstUser().getId() == userId && friend.getSecondUser().getId() == anotherUserId)
              || (friend.getFirstUser().getId() == anotherUserId && friend.getSecondUser().getId() == userId)) {
        friendRepository.deleteById(friend.getId());
        return;
      }
    }
  }


  @GetMapping("/api/friend/user/{userId}")
  public List<User> findFriendList(@PathVariable("userId") int userId) {
    List<User> users = new ArrayList<>();
    List<Friend> friendHistory = (List<Friend>) friendRepository.findAll();

    for (Friend friend : friendHistory) {
      if (friend.getFirstUser().getId() == userId && friend.isStatus() && friend.getSecondUser().isStatus()) {
        users.add(friend.getSecondUser());
      } else if (friend.getSecondUser().getId() == userId && friend.isStatus() && friend.getFirstUser().isStatus()) {
        users.add(friend.getFirstUser());
      }
    }
    return users;
  }

}