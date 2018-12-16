package project.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Business {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String yelpId;

  private String name;
  private String category;
  //FOOD, COFFEE, MUSIC, SHOPPING, ART, MOVIE
  private String address;
  private Double latitude;
  private Double longitude;
  private boolean isOpen = false;
  private String website;
  private String phone;

  @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Schedule> schedules;

  @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts;

  public String getYelpId() {
    return yelpId;
  }

  public void setYelpId(String yelpId) {
    this.yelpId = yelpId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }


  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public boolean isOpen() {
    return isOpen;
  }

  public void setOpen(boolean open) {
    isOpen = open;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public List<Schedule> getSchedules() {
    return schedules;
  }

  public void setSchedules(List<Schedule> schedules) {
    this.schedules = schedules;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }
}