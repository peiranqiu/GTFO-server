package project.models;

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
  @Column(name = "user_id")
  private int id;

  private String insId;
  private String username;
  private String picture;
  private String token;


  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
  private List<Chat> chats = new ArrayList<>();

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }


  public List<Chat> getChats() {
    return chats;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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

}
