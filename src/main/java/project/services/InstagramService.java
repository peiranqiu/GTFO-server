package project.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import project.models.Business;
import project.models.Post;
import project.models.Schedule;
import project.models.User;
import project.repositories.BusinessRepository;
import project.repositories.PostRepository;
import project.repositories.ScheduleRepository;
import project.repositories.UserRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class InstagramService {

  ObjectMapper mapper = new ObjectMapper();
  OkHttpClient client = new OkHttpClient();
  String INSTAGRAM_URL = "https://api.instagram.com/v1/users/self/";
  String YELP_URL = "https://api.yelp.com/v3/businesses/";
  String YELP_API_KEY = "apR1p2KGGz8v52Q1KxImt4xNsJ04-MARDS7ssayXkXx8EksSZxOLa1Wfa4"
          + "Fo29v6G0ftWYT6CsWyaknfttuSdu9n14iXEpomQ6-l3ExoZf7ra3kUVYNKeAq3EKpaW3Yx";

  @Autowired
  UserRepository userRepository;
  @Autowired
  PostRepository postRepository;
  @Autowired
  BusinessRepository businessRepository;
  @Autowired
  ScheduleRepository scheduleRepository;

  @PostMapping("/api/instagram/user/{token}")
  public User createUser(@PathVariable("token") String token)
          throws IOException, JSONException {

    String url = INSTAGRAM_URL + "?access_token=" + token;

    Request request = new Request.Builder().url(url).get().addHeader("cache-control", "no-cache").build();

    Response response = client.newCall(request).execute();

    JSONObject responseObject = new JSONObject(response.body().string().trim());
    if (responseObject.has("data")) {
      JSONObject object = responseObject.getJSONObject("data");
      String insId = object.getString("id");
      Optional<User> data = userRepository.findByInsId(insId);
      if (data.isPresent()) {
        return data.get();
      } else {
        User user = new User();
        user.setInsId(insId);
        user.setUsername(object.getString("username"));
        user.setPicture(object.getString("profile_picture"));
        user.setToken(token);
        user.setStatus(true);

        return userRepository.save(user);
      }
    }
    return null;
  }

  @ResponseBody
  @GetMapping("/api/instagram/user/update")
  public void updateAvatar()
          throws IOException, JSONException {
    Iterable<User> users = userRepository.findAll();
    for (User user : users) {
      String url = INSTAGRAM_URL + "?access_token=" + user.getToken();

      Request request = new Request.Builder().url(url).get().addHeader("cache-control", "no-cache").build();

      Response response = client.newCall(request).execute();

      JSONObject object = new JSONObject(response.body().string().trim()).getJSONObject("data");


      user.setPicture(object.getString("profile_picture"));
      userRepository.save(user);
    }
  }

  @ResponseBody
  @GetMapping("/api/instagram/newpost")
  public void updateAllPosts()
          throws IOException, JSONException {

    List<Post> posts = new ArrayList<>();
    for (User u : userRepository.findAll()) {
      if(u.isStatus()) {
        findNewPostsForUser(u.getId(), u);
      }
    }
  }

  @PostMapping("/api/instagram/user/{userId}/post")
  public void findNewPostsForUser(@PathVariable("userId") int userId, User user) throws IOException, JSONException {
    String url = INSTAGRAM_URL + "media/recent/?access_token=";
    url = url + user.getToken();

    Request request = new Request.Builder().url(url).get().addHeader("cache-control", "no-cache").build();

    Response response = client.newCall(request).execute();

    JSONObject jsonObject = new JSONObject(response.body().string().trim());
    if (jsonObject.has("data")) {
      JSONArray myResponse = (JSONArray) jsonObject.get("data");

      int index = 0;
      while (index < myResponse.length()) {
        if (postRepository.findByInsId(myResponse.getJSONObject(index).getString("id")).isPresent()) {
          break;
        }
        index++;
      }
      for (int i = index - 1; i >= 0; i--) {
        JSONObject object = myResponse.getJSONObject(i);
        Optional<Post> opt = postRepository.findByInsId(object.getString("id"));
        if (opt.isPresent() || object.isNull("location") || !object.getString("type").equals("image")) {
          continue;
        }
        String name = object.getJSONObject("location").getString("name");
        Double lat = object.getJSONObject("location").getDouble("latitude");
        Double lng = object.getJSONObject("location").getDouble("longitude");
        Business business = findBusinessByMatching(name, lat, lng);
        if (business != null) {
          Optional<Business> data = businessRepository.findByYelpId(business.getYelpId());
          if (data.isPresent()) {
            createPost(object, data.get(), user);
          } else {
            Business savedBusiness = businessRepository.save(business);
            saveScheduleForBusiness(business.getYelpId(), savedBusiness);
            createPost(object, savedBusiness, user);
          }
        }
      }
    }
  }

  @PostMapping("/api/instagram/post/create")
  public void createPost(JSONObject object, Business business, User user) throws IOException, JSONException {
    Post post = new Post();
    post.setInsId(object.getString("id"));
    post.setUser(user);
    post.setBusiness(business);
    if (!object.isNull("caption")) {
      post.setContent(object.getJSONObject("caption").getString("text"));
    }
    post.setPhoto(object.getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
    postRepository.save(post);
  }

  @ResponseBody
  @GetMapping("/api/yelp/business/name/{name}")
  public Business findBusinessByMatching(@PathVariable("name") String name, Double lat, Double lng)
          throws IOException, JSONException {
    String url = YELP_URL + "search";
    url = url + "?term=" + name + "&latitude=" + lat + "&longitude=" + lng;

    Request request = new Request.Builder().url(url).get().addHeader("authorization", "Bearer " + YELP_API_KEY)
            .addHeader("cache-control", "no-cache").build();

    Response response = client.newCall(request).execute();


    JSONObject jsonObject = new JSONObject(response.body().string().trim());


    if (jsonObject.isNull("businesses")) {
      return null;
    }
    JSONArray myResponse = (JSONArray) jsonObject.get("businesses");
    if (myResponse.length() < 1) {
      return null;
    }
    String yelpId = myResponse.getJSONObject(0).getString("id");
    String yelpName = myResponse.getJSONObject(0).getString("name");
    if (yelpName.equals(name) || yelpName.contains(name)) {
      Business business = findBusinessByYelpId(yelpId);
      return business;
    }
    return null;
  }

  @GetMapping("/api/yelp/business/{yelpId}")
  public Business findBusinessByYelpId(@PathVariable("yelpId") String yelpId) throws IOException, JSONException {
    Request request = new Request.Builder().url(YELP_URL + yelpId).get()
            .addHeader("authorization", "Bearer " + YELP_API_KEY).addHeader("cache-control", "no-cache").build();

    Response response = client.newCall(request).execute();

    JSONObject jsonObject = new JSONObject(response.body().string().trim());

    Business business = jsonToBusiness(jsonObject);
    return business;
  }

  @PostMapping("/api/yelp/business/{yelpId}/schedule")
  public void saveScheduleForBusiness(@PathVariable("yelpId") String yelpId, Business business) throws IOException, JSONException {
    Request request = new Request.Builder().url(YELP_URL + yelpId).get()
            .addHeader("authorization", "Bearer " + YELP_API_KEY).addHeader("cache-control", "no-cache").build();

    Response response = client.newCall(request).execute();

    JSONObject object = new JSONObject(response.body().string().trim());

    if (!object.isNull("hours")) {
      JSONArray schedules = object.getJSONArray("hours").getJSONObject(0).getJSONArray("open");
      for (int i = 0; i < schedules.length(); i++) {
        Schedule schedule = new Schedule();
        schedule.setStart(Integer.parseInt(schedules.getJSONObject(i).getString("start")));
        schedule.setEnd(Integer.parseInt(schedules.getJSONObject(i).getString("end")));
        schedule.setDay(schedules.getJSONObject(i).getInt("day"));
        schedule.setBusiness(business);
        scheduleRepository.save(schedule);
      }
    }
  }

  public Business jsonToBusiness(JSONObject object) throws JSONException {

    Business business = new Business();

    business.setYelpId(object.getString("id"));
    business.setName(object.getString("name"));
    business.setWebsite(object.getString("url"));
    business.setPhone(object.getString("phone"));

    //address
    String address = "";
    if (!object.isNull("location")) {
      JSONArray data = object.getJSONObject("location").getJSONArray("display_address");
      for (int i = 0; i < data.length(); i++) {
        address = address + data.get(i).toString() + " ";
      }
    }
    business.setAddress(address);

    business.setLatitude(object.getJSONObject("coordinates").getDouble("latitude"));
    business.setLongitude(object.getJSONObject("coordinates").getDouble("longitude"));

    //categories
    //FOOD, COFFEE, MUSIC, SHOPPING, ART, MOVIE
    JSONArray myResponse = object.getJSONArray("categories");
    String category = "";
    for (int i = 0; i < myResponse.length(); i++) {
      String str = myResponse.getJSONObject(i).getString("alias");
      if (str.contains("bagel") || str.contains("chinese") || str.contains("food")
              || str.contains("brunch") || str.contains("restaurant") || str.contains("breakfast")
              || str.contains("cafe") || str.contains("chicken") || str.contains("italian")
              || str.contains("american") || str.contains("cake") || str.contains("french")
              || str.contains("japanese") || str.contains("korean") || str.contains("mexican")) {
        category = "food";
        break;
      }
      if (str.contains("tea") || str.contains("coffee") || str.contains("juice") || str.contains("bar")) {
        category = "coffee";
        break;
      }
      if (str.contains("jazz") || str.contains("pubs") || str.contains("dance")
              || str.contains("club") || str.contains("piano")) {
        category = "music";
        break;
      }
      if (str.contains("shop") || str.contains("stores") || str.contains("shoes") || str.contains("cloth")) {
        category = "shopping";
        break;
      }
      if ((str.contains("festival") || str.contains("art") || str.contains("museum")
              || str.contains("culture") || str.contains("galleries")) && !str.contains("apartment")) {
        category = "art";
        break;
      }
      if (str.contains("theater") || str.contains("opera") || str.contains("movie")) {
        category = "movie";
        break;
      }
    }
    business.setCategory(category);

    return business;
  }
}