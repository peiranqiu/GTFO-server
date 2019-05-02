package project.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import project.models.BlockedBusinessId;
import project.models.Business;
import project.models.Chat;
import project.models.Message;
import project.models.User;
import project.repositories.BlockedBusinessIdRepository;
import project.repositories.UserRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class UserService {
  @Autowired
  UserRepository userRepository;
  @Autowired
  BlockedBusinessIdRepository blockedBusinessIdRepository;

  @GetMapping("/api/user/{userId}")
  public User findUserById(@PathVariable("userId") int userId) {
    Optional<User> data = userRepository.findById(userId);
    if (data.isPresent()) {
      return data.get();
    } else {
      return null;
    }
  }

  @PutMapping("/api/user/{userId}/update")
  public User updateUser(@PathVariable("userId") int userId, @RequestBody User newUser) {
    Optional<User> data = userRepository.findById(userId);
    if (data.isPresent()) {
      User user = data.get();
      user.setPushToken(newUser.getPushToken());
      return userRepository.save(user);
    } else {
      return null;
    }
  }

  @PutMapping("/api/user/{userId}/status/{status}")
  public User ChangeUserStatus(@PathVariable("userId") int userId, @PathVariable("status") String status) {

    Optional<User> data = userRepository.findById(userId);
    if (data.isPresent()) {
      User user = data.get();
      if (status.equals("block")) {
        user.setStatus(false);
      } else {
        user.setStatus(true);
      }
      return userRepository.save(user);
    }
    return null;
  }

  @GetMapping("/api/user")
  public Iterable<User> findAllUsers() {
    return userRepository.findAll();
  }

  @PostMapping("/api/user/{userId}/block/{businessId}")
  public BlockedBusinessId BlockBusiness(@PathVariable("userId") int userId, @PathVariable("businessId") int businessId) throws IOException {
    Optional<User> data = userRepository.findById(userId);
    if (data.isPresent()) {
      User user = data.get();
      List<BlockedBusinessId> list = user.getBlockedBusinessId();
      Boolean exist = false;
      for(BlockedBusinessId b: list) {
        if(b.getBusinessId() == businessId) {
          exist = true;
        }
      }
      if(!exist) {
        BlockedBusinessId blockedBusinessId = new BlockedBusinessId();
        blockedBusinessId.setBusinessId(businessId);
        blockedBusinessId.setUser(user);
        return blockedBusinessIdRepository.save(blockedBusinessId);
      }
    }
    return null;
  }
}
