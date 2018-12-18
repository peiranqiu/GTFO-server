package project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int _id;

  @OneToOne
  private User user;

  private int businessId;

  private String text;
  private Date createdAt;

  @ManyToOne(cascade = CascadeType.ALL)
  @JsonIgnore
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Chat chat;


  public Chat getChat() {
    return chat;
  }

  public void setChat(Chat chat) {
    this.chat = chat;
  }

  @JsonProperty("_id")
  public int getId() {
    return _id;
  }

  public void setId(int _id) {
    this._id = _id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public int getBusinessId() {
    return businessId;
  }

  public void setBusinessId(int businessId) {
    this.businessId = businessId;
  }
}
