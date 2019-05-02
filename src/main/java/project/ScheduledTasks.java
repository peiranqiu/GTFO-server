package project;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import project.models.User;
import project.repositories.UserRepository;

@Component
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class ScheduledTasks {
  String URL = "https://exp.host/--/api/v2/push/send";
  String SERVER = "http://test-gtfo.us-east-2.elasticbeanstalk.com/api/";
  String INSTAGRAM_URL = "https://api.instagram.com/v1/users/self/";
  ObjectMapper mapper = new ObjectMapper();
  OkHttpClient client = new OkHttpClient();

  @Autowired
  UserRepository userRepository;

  // 10 min
  @Scheduled(fixedRate = 1000 * 60 * 10)
  public void updateAll() throws IOException, JSONException {
    String url = SERVER + "instagram/newpost";

    Request request = new Request.Builder().url(url).get().addHeader("cache-control", "no-cache").build();

    Response response = client.newCall(request).execute();
  }

  // 1 day
  @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
  public void updateAvatar() throws IOException, JSONException {
    Iterable<User> users = userRepository.findAll();
    for (User user : users) {
      if(user.isStatus()) {
        String url = INSTAGRAM_URL + "?access_token=" + user.getToken();
        Request request = new Request.Builder().url(url).get().addHeader("cache-control", "no-cache").build();
        Response response = client.newCall(request).execute();
        JSONObject object = new JSONObject(response.body().string().trim());
        if (object.has("data")) {
          user.setPicture(object.getJSONObject("data").getString("profile_picture"));
          userRepository.save(user);
        }
      }
    }
  }

  // 10 min
  @Scheduled(fixedRate = 1000 * 60 * 10)
  public void sendReminder() throws IOException, JSONException, ParseException {
    String url = SERVER + "chat";
    Request request = new Request.Builder().url(url).get().addHeader("cache-control", "no-cache").build();
    Response response = client.newCall(request).execute();
    JSONArray myResponse = new JSONArray(response.body().string());
    for (int i = 0; i < myResponse.length(); i++) {
      JSONObject chat = myResponse.getJSONObject(i);
      String time = chat.get("time").toString();
      if(!time.equals("null")) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date date  = formatter.parse(time);
        Calendar now = Calendar.getInstance();
        long t= now.getTimeInMillis();
        Date nextDate = new Date(t + 1000 * 60 * 10);
        if(date.compareTo(new Date()) > 0 && date.compareTo(nextDate) < 0 ) {
          JSONArray users = chat.getJSONArray("users");
          for (int j = 0; j < users.length(); j++) {
            String pushToken = users.getJSONObject(j).get("pushToken").toString();
            if (!pushToken.equals("null")) {
              JSONObject obj = new JSONObject();
              obj.put("to", pushToken);
              obj.put("badge", 1);
              obj.put("title", "Time to get out!");
              long diffMinutes = (date.getTime() - t) / (60 * 1000) % 60;
              String str = "in " + diffMinutes + " minute";
              if(diffMinutes == 0) {
                str = "now";
              }
              obj.put("body", chat.getString("name") + " is happening " + str + " at "
                      + chat.getString("address"));
              MediaType JSON
                      = MediaType.parse("application/json; charset=utf-8");
              okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON, obj.toString());
              Request pushRequest = new Request.Builder().url(URL)
                      .post(requestBody)
                      .addHeader("content-Type", "application/json")
                      .addHeader("host", "exp.host")
                      .addHeader("accept-encoding", "gzip, deflate")
                      .addHeader("accept", "application/json")
                      .build();
              Response pushResponse = client.newCall(pushRequest).execute();
            }
          }
        }
      }
    }
  }
}