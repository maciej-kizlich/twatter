package pl.maciejkizlich.hsbc.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;


@Entity
public class User {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  private String username;
  
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  @JsonBackReference
  private Set<Twatt> twatts = new HashSet<>();
  
  @OneToMany
  @JoinTable(
      name="followees",
      joinColumns = @JoinColumn(name="id")
  )
  @JsonIgnore
  private Set<User> followedUsers = new HashSet<>();
  
  User() {}
  
  public User(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public Set<Twatt> getTwatts() {
    return twatts;
  }

  public long getId() {
    return id;
  }

  public void follow(User user) {
    followedUsers.add(user);
  }

  public Set<User> getFollowedUsers() {
    return followedUsers;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final User other = (User) obj;
    return Objects.equal(this.id, other.id) && Objects.equal(this.username, other.username);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.id, this.username);
  }

}
