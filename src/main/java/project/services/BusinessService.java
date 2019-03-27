package project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
          @PathVariable("businessId") int businessId) {
    Optional<Business> data = businessRepository.findById(businessId);
    if (data.isPresent()) {
      return data.get().getSchedules();
    }
    return null;
  }

  @GetMapping("/api/business/{businessId}/post")
  public List<Post> findPostsForBusiness(
          @PathVariable("businessId") int businessId) {
    Optional<Business> data = businessRepository.findById(businessId);
    if (data.isPresent()) {
      return data.get().getPosts();
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

  @GetMapping("/api/post/{postId}/business")
  public Business findBusinessForPost(@PathVariable("postId") int postId) {
    Optional<Post> data = postRepository.findById(postId);
    if (data.isPresent()) {
      Post post = data.get();
      return post.getBusiness();
    }
    return null;
  }

  @DeleteMapping("/api/business/{businessId}/delete")
  public void deleteBusiness(@PathVariable("businessId") int businessId) {
    businessRepository.deleteById(businessId);
  }

  @PutMapping("/api/business/{businessId}/sticky")
  public Business StickyBusiness(@PathVariable("businessId") int businessId) {

    Optional<Business> data = businessRepository.findById(businessId);
    if (data.isPresent()) {
      Business business = data.get();
      int o = business.getOrder();
      List<Business> list = (List<Business>) businessRepository.findAll();
      for (Business b : list) {
        int order = b.getOrder();
        if (b.getId() == businessId || order < 0) {
          continue;
        }
        if (o >= 0) {
          if (order < o) {
            b.setOrder(order + 1);
          }
        } else {
          if (order == 4) {
            b.setOrder(-1);
          } else {
            b.setOrder(order + 1);
          }
        }
        businessRepository.save(b);
      }
      business.setOrder(0);
      return businessRepository.save(business);
    }
    return null;
  }

  @PutMapping("/api/business/{businessId}/reset")
  public void ResetBusiness(@PathVariable("businessId") int businessId) {

    Optional<Business> data = businessRepository.findById(businessId);

    if (data.isPresent()) {
      Business business = data.get();
      int order = business.getOrder();
      business.setOrder(-1);
      businessRepository.save(business);
      List<Business> list = (List<Business>) businessRepository.findAll();
      boolean empty = false;
      for(int i = list.size()-1; i >= 0; i--) {
        Business b = list.get(i);
        int o = b.getOrder();
        if(o > order) {
          b.setOrder(o - 1);
          businessRepository.save(b);
        }
        else if(!empty) {
          empty = true;
          b.setOrder(4);
          businessRepository.save(b);
        }
      }


    }
  }

  @PutMapping("/api/business/{businessId}/category/{category}")
  public Business ChangeCategory(@PathVariable("businessId") int businessId, @PathVariable("category") String category) {

    Optional<Business> data = businessRepository.findById(businessId);
    if (data.isPresent()) {
      Business business = data.get();
      if (category.equals("none")) {
        business.setCategory("");
      } else {
        business.setCategory(category);
      }
      return businessRepository.save(business);
    }
    return null;
  }
}
