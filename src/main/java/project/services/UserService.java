package project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import project.models.User;
import project.repositories.UserRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class UserService {
  @Autowired
  UserRepository userRepository;

  @GetMapping("/api/user/{userId}")
  public User findUserById(@PathVariable("userId") int userId) {
    Optional<User> data = userRepository.findById(userId);
    if (data.isPresent()) {
      return data.get();
    } else {
      return null;
    }
  }


  @GetMapping("/api/user")
  public Iterable<User> findAllUsers() {
    return userRepository.findAll();
  }


}
