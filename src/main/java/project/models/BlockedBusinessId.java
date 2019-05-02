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
public class BlockedBusinessId {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private int businessId;

  @ManyToOne(cascade = CascadeType.ALL)
  @JsonIgnore
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getBusinessId() {
    return businessId;
  }

  public void setBusinessId(int businessId) {
    this.businessId = businessId;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
