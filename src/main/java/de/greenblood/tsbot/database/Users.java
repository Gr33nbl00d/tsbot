package de.greenblood.tsbot.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "users")
@Component
public class Users {

  @Id
  @Column(name = "username", nullable = false, unique = true)
  private String username;
  @Column(name = "password", nullable = false)
  @NotNull
  private String password;
  @OneToMany(mappedBy = "username",fetch = FetchType.EAGER)
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
            '}';
  }
}
