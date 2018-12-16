package project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private int userId;
  private int postId;
  private String content;

  @ManyToOne(cascade = CascadeType.ALL)
  @JsonIgnore
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Chat chat;


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }


  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }


  public Chat getChat() {
    return chat;
  }

  public void setChat(Chat chat) {
    this.chat = chat;
  }

  public int getPostId() {
    return postId;
  }

  public void setPostId(int postId) {
    this.postId = postId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }
}
