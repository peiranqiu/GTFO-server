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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
// @CrossOrigin(origins = "https://foodtruckmapper.herokuapp.com",
// allowCredentials = "true")
public class InstagramService {

  ObjectMapper mapper = new ObjectMapper();
  OkHttpClient client = new OkHttpClient();
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


  @ResponseBody
  @GetMapping("/api/instagram/user/create")
  public User createUser(String token)
          throws IOException, JSONException {

    String url = "https://api.instagram.com/v1/users/self/?access_token=" + token;

    Request request = new Request.Builder().url(url).get().addHeader("cache-control", "no-cache").build();

    Response response = client.newCall(request).execute();

    JSONObject object = new JSONObject(response.body().string().trim()).getJSONObject("data");

    User user = new User();
    user.setInsId(object.getString("id"));
    user.setUsername(object.getString("username"));
    user.setPicture(object.getString("profile_picture"));
    user.setToken(token);

    return userRepository.save(user);

  }

  @ResponseBody
  @GetMapping("/api/instagram/post")
  public Iterable<Post> findAllPosts()
          throws IOException, JSONException {

    return postRepository.findAll();
  }

  @ResponseBody
  @GetMapping("/api/instagram/newpost")
  public void updateAllPosts()
          throws IOException, JSONException {

    List<Post> posts = new ArrayList<>();
    for (User u : userRepository.findAll()) {
      findNewPostsForUser(u.getId(), u);
    }
  }

  @GetMapping("/api/instagram/user/{userId}/post")
  public void findNewPostsForUser(@PathVariable("userId") int userId, User user) throws IOException, JSONException {
    String url = "https://api.instagram.com/v1/users/self/media/recent/?access_token=";
    url = url + user.getToken();

    Request request = new Request.Builder().url(url).get().addHeader("cache-control", "no-cache").build();

    Response response = client.newCall(request).execute();

    JSONObject jsonObject = new JSONObject(response.body().string().trim());
    JSONArray myResponse = (JSONArray) jsonObject.get("data");

    int index = 0;
    while (index < myResponse.length()) {
      Optional<Post> data = postRepository.findByInsId(myResponse.getJSONObject(index).getString("id"));
      if (data.isPresent()) {
        break;
      }
      index++;
    }
    for (int i = index - 1; i >= 0; i--) {
      JSONObject object = myResponse.getJSONObject(i);
      String name = object.getJSONObject("location").getString("name");
      Double lat = object.getJSONObject("location").getDouble("latitude");
      Double lng = object.getJSONObject("location").getDouble("longitude");
      Business business = findBusinessByMatching(name, lat, lng);
      if (business != null) {
        Optional<Business> data = businessRepository.findByYelpId(business.getYelpId());
        if (data.isPresent()) {
          Post post = createPost(object, business, userId);
        } else {
          Business savedBusiness = businessRepository.save(business);
          saveScheduleForBusiness(business.getYelpId(), savedBusiness);
          Post post = createPost(object, savedBusiness, userId);
        }
      }
    }
  }

  @GetMapping("/api/instagram/post/create")
  public Post createPost(JSONObject object, Business business, int userId) throws IOException, JSONException {
    Post post = new Post();
    post.setInsId(object.getString("id"));
    post.setUserId(userId);
    post.setBusiness(business);
    if (!object.isNull("caption")) {
      post.setContent(object.getJSONObject("caption").getString("text"));
    }
    post.setPhoto(object.getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
    return postRepository.save(post);
  }

  @ResponseBody
  @GetMapping("/api/yelp/business/name/{name}")
  public Business findBusinessByMatching(@PathVariable("name") String name, Double lat, Double lng)
          throws IOException, JSONException {
    String url = "https://api.yelp.com/v3/businesses/search";
    url = url + "?term=" + name + "&latitude=" + lat + "&longitude=" + lng;

    Request request = new Request.Builder().url(url).get().addHeader("authorization", "Bearer " + YELP_API_KEY)
            .addHeader("cache-control", "no-cache").build();

    Response response = client.newCall(request).execute();

    JSONObject jsonObject = new JSONObject(response.body().string().trim());
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
    Request request = new Request.Builder().url("https://api.yelp.com/v3/businesses/" + yelpId).get()
            .addHeader("authorization", "Bearer " + YELP_API_KEY).addHeader("cache-control", "no-cache").build();

    Response response = client.newCall(request).execute();

    JSONObject jsonObject = new JSONObject(response.body().string().trim());

    Business business = jsonToBusiness(jsonObject);
    return business;
  }

  @GetMapping("/api/yelp/business/{yelpId}/schedule")
  public void saveScheduleForBusiness(@PathVariable("yelpId") String yelpId, Business business) throws IOException, JSONException {
    Request request = new Request.Builder().url("https://api.yelp.com/v3/businesses/" + yelpId).get()
            .addHeader("authorization", "Bearer " + YELP_API_KEY).addHeader("cache-control", "no-cache").build();

    Response response = client.newCall(request).execute();

    JSONObject object = new JSONObject(response.body().string().trim());

    Iterator<String> keys = object.keys();

    while(keys.hasNext()) {
      String key = keys.next();
      if (key.equals("hours")) {
        JSONArray schedules = object.getJSONArray("hours").getJSONObject(0).getJSONArray("open");
        for(int i = 0; i<schedules.length(); i++) {
          Schedule schedule = new Schedule();
          schedule.setStart(Integer.parseInt(schedules.getJSONObject(i).getString("start")));
          schedule.setEnd(Integer.parseInt(schedules.getJSONObject(i).getString("end")));
          schedule.setDay(schedules.getJSONObject(i).getInt("day"));
          schedule.setBusiness(business);
          scheduleRepository.save(schedule);
        }
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
    JSONArray address = object.getJSONObject("location").getJSONArray("display_address");
    business.setAddress(address.get(0).toString() + ", " + address.get(1).toString());
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
      if (str.contains("festival") || str.contains("art") || str.contains("museum")
              || str.contains("culture") || str.contains("galleries")) {
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