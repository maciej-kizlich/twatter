package pl.maciejkizlich.hsbc.model;

import com.google.common.annotations.VisibleForTesting;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.base.Objects;


@Entity
public class Twatt {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JsonManagedReference
  private User user;
  
  @Size(max = 140, message = "Twatt must not be longer than 140 characters")
  private String content;
  
  //that's bad, I know
  @VisibleForTesting
  public LocalDateTime timestamp = LocalDateTime.now();
  
  Twatt() {}

  public Twatt(String content, User user) {
    this.content = content;
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public String getContent() {
    return content;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final Twatt other = (Twatt) obj;
    return Objects.equal(this.id, other.id) && Objects.equal(this.content, other.content);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.id, this.content);
  }

}
