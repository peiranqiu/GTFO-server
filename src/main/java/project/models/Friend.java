package project.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Friend {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @OneToOne
  private User firstUser;

  @OneToOne
  private User secondUser;

  private boolean status = false;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }


  public boolean isStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public User getFirstUser() {
    return firstUser;
  }

  public void setFirstUser(User firstUser) {
    this.firstUser = firstUser;
  }

  public User getSecondUser() {
    return secondUser;
  }

  public void setSecondUser(User secondUser) {
    this.secondUser = secondUser;
  }
}
