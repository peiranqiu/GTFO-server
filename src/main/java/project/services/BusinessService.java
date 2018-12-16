package project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import project.models.Business;
import project.models.Post;
import project.models.Schedule;
import project.repositories.BusinessRepository;
import project.repositories.PostRepository;
import project.repositories.ScheduleRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class BusinessService {
  @Autowired
  BusinessRepository businessRepository;
  @Autowired
  ScheduleRepository scheduleRepository;
  @Autowired
  PostRepository postRepository;

  @GetMapping("/api/business")
  public List<Business> findAllBusinesses() {
    return (List<Business>) businessRepository.findAll();
  }

  @GetMapping("/api/business/{businessId}")
  public Business findBusinessById(@PathVariable("businessId") int businessId) {
    Optional<Business> data = businessRepository.findById(businessId);
    if (data.isPresent()) {
      return data.get();
    }
    return null;
  }

  @GetMapping("/api/business/{businessId}/schedule")
  public List<Schedule> findSchedulesForBusiness(
          @PathVariable("businessIdId") int businessId) {
    Optional<Business> data = businessRepository.findById(businessId);
    if (data.isPresent()) {
      Business business = data.get();
      return business.getSchedules();
    }
    return null;
  }

  @GetMapping("/api/business/{businessId}/post")
  public List<Post> findPostsForBusiness(
          @PathVariable("businessId") int businessId) {
    Optional<Business> data = businessRepository.findById(businessId);
    if (data.isPresent()) {
      Business business = data.get();
      return business.getPosts();
    }
    return null;
  }

  @GetMapping("/api/post")
  public List<Post> findAllPosts() {
    return (List<Post>) postRepository.findAll();
  }

  @GetMapping("/api/post/{postId}")
  public Post findPostById(@PathVariable("postId") int postId) {
    Optional<Post> data = postRepository.findById(postId);
    if (data.isPresent()) {
      return data.get();
    }
    return null;
  }
}