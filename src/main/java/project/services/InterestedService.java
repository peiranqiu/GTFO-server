package project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import project.models.Business;
import project.models.Interested;
import project.models.User;
import project.repositories.BusinessRepository;
import project.repositories.InterestedRepository;
import project.repositories.UserRepository;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class InterestedService {
  @Autowired
  InterestedRepository interestedRepository;
  @Autowired
  BusinessRepository businessRepository;
  @Autowired
  UserRepository userRepository;

  @PostMapping("/api/interested/{businessId}")
  public void userLikesBusiness(@PathVariable("businessId") int businessId, @RequestBody User user) {
    Optional<Business> data = businessRepository.findById(businessId);
    if (data.isPresent()) {
      Business business = data.get();

      List<Interested> intrHistory = (List<Interested>) interestedRepository.findAll();
      for (Interested intr : intrHistory) {
        if (intr.getUser().getId() == user.getId()
                && intr.getBusiness().getId() == businessId) {
          interestedRepository.deleteById(intr.getId());
          return;
        }
      }

      Interested intr = new Interested();
      intr.setBusiness(business);
      intr.setUser(user);
      interestedRepository.save(intr);
    }
  }

  @GetMapping("/api/interested/{businessId}/{userId}")
  public boolean findIfInterested(@PathVariable("businessId") int businessId, @PathVariable("userId") int userId) {
    Optional<Business> data = businessRepository.findById(businessId);

    if (data.isPresent()) {
      Business business = data.get();

      List<Interested> intrHistory = (List<Interested>) interestedRepository.findAll();
      for (Interested intr : intrHistory) {
        if (intr.getUser().getId() == userId
                && intr.getBusiness().getId() == businessId) {
          return true;
        }
      }
    }
    return false;
  }

  @GetMapping("/api/interested/business/{businessId}")
  public List<User> findInterestedUsersForBusiness(@PathVariable("businessId") int businessId) {
    List<User> users = new ArrayList<User>();
    List<Interested> intrHistory = (List<Interested>) interestedRepository.findAll();

    for (Interested intr : intrHistory) {
      if (intr.getBusiness().getId() == businessId && intr.getUser().isStatus()) {
        users.add(intr.getUser());
      }
    }
    return users;
  }

  @GetMapping("/api/interested/user/{userId}")
  public List<Business> findMarkedBusinessForUser(@PathVariable("userId") int userId) {
    List<Business> businesses = new ArrayList<Business>();
    List<Interested> intrHistory = (List<Interested>) interestedRepository.findAll();
    for (Interested intr : intrHistory) {
      if (intr.getUser().getId() == userId && !intr.getBusiness().isOpen()) {
        businesses.add(intr.getBusiness());
      }
    }
    return businesses;
  }
}