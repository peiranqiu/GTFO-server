package project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int _id;

  private String insId;
  private String name;
  private String avatar;
  private String token;
  private String pushToken = null;


  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
  @JsonIgnore
  private List<Chat> chats = new ArrayList<>();


  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }


  public List<Chat> getChats() {
    return chats;
  }

  @JsonProperty("_id")
  public int getId() {
    return _id;
  }

  public void setId(int _id) {
    this._id = _id;
  }

  @JsonProperty("name")
  public String getUsername() {
    return name;
  }

  public void setUsername(String name) {
    this.name = name;
  }

  @JsonProperty("avatar")
  public String getPicture() {
    return avatar;
  }

  public void setPicture(String avatar) {
    this.avatar = avatar;
  }

  public String getInsId() {
    return insId;
  }

  public void setInsId(String insId) {
    this.insId = insId;
  }

  public void setChats(List<Chat> chats) {
    this.chats = chats;
  }

  public String getPushToken() {
    return pushToken;
  }

  public void setPushToken(String pushToken) {
    this.pushToken = pushToken;
  }
}
