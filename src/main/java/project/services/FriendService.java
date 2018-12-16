package project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import project.models.Friend;
import project.models.User;
import project.repositories.FriendRepository;
import project.repositories.UserRepository;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class FriendService {
  @Autowired
  FriendRepository friendRepository;
  @Autowired
  UserRepository userRepository;

  @PostMapping("/api/friend/request/{userId}")
  public void userSendRequest(@PathVariable("userId") int userId, User firstUser) {
    Optional<User> data = userRepository.findById(userId);

    if (data.isPresent()) {
      User secondUser = data.get();

      Friend friend = new Friend();
      friend.setFirstUser(firstUser);
      friend.setSecondUser(secondUser);
      friend.setStatus(false);
      friendRepository.save(friend);
    }
  }


  @GetMapping("/api/friend/request/{userId}")
  public List<Friend> findFriendRequests(@PathVariable("userId") int userId) {
    List<Friend> friends = new ArrayList<Friend>();
    List<Friend> friendHistory = (List<Friend>) friendRepository.findAll();

    for (Friend friend : friendHistory) {
      if (friend.getSecondUser().getId() == userId && !friend.isStatus()) {
        friends.add(friend);
      }
    }
    return friends;
  }


  @PostMapping("/api/friend/{friendId}")
  public void userAcceptRequest(@PathVariable("friendId") int friendId) {

    Optional<Friend> data = friendRepository.findById(friendId);
    if (data.isPresent()) {
      Friend friend = data.get();
      friend.setStatus(true);
      friendRepository.save(friend);
    }
  }

  @DeleteMapping("/api/friend/{friendId}")
  public void userDeleteRequest(@PathVariable("friendId") int friendId) {
    friendRepository.deleteById(friendId);
  }


  @GetMapping("/api/friend/user/{userId}")
  public List<User> findFriendList(@PathVariable("userId") int userId) {
    List<User> users = new ArrayList<>();
    List<Friend> friendHistory = (List<Friend>) friendRepository.findAll();

    for (Friend friend : friendHistory) {
      if (friend.getFirstUser().getId() == userId && friend.isStatus()) {
        users.add(friend.getSecondUser());
      } else if (friend.getSecondUser().getId() == userId && friend.isStatus()) {
        users.add(friend.getFirstUser());
      }
    }
    return users;
  }

}