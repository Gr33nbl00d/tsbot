package de.greenblood.tsbot.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "users")
@Component
public class Users {

  @Id
  private String username;
  private String password;
  private boolean enabled;
  @OneToMany(mappedBy = "username")
  private List<Authorities> authorities;

  public List<Authorities> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(List<Authorities> authorities) {
    this.authorities = authorities;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @JsonIgnore
  public String getPassword() {
    return password;
  }

  @JsonProperty
  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Users users = (Users) o;
    return username.equals(users.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }

  @Override
  public String toString() {
    return "Users{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", enabled=" + enabled +
            '}';
  }
}