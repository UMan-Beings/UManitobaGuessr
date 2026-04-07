package com.umanbeing.umg.models;

import com.umanbeing.umg.domain.Role;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Entity class representing a user. This class is mapped to the "User" table in the database.
 *
 * <p>It has the following attributes:
 * <li>{@link Long} userId
 * <li>{@link String} username
 * <li>{@link String} email
 * <li>{@link String} passwordHash
 * <li>{@link String} profileImageUrl
 * <li>{@link Role} role
 */
@Entity
@Table(name = "\"User\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"userId\"")
  private Long userId;

  @Column(name = "\"username\"", nullable = false, unique = true)
  private String username;

  @Column(name = "\"email\"", nullable = false, unique = true)
  private String email;

  @Column(name = "\"passwordHash\"", nullable = false)
  private String passwordHash;

  @Column(name = "\"profileImageUrl\"")
  private String profileImageUrl;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Game> games = new ArrayList<>();

  @Column(name = "\"role\"", nullable = true)
  @Enumerated(EnumType.STRING)
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role == null ? List.of() : List.of(role);
  }

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return UserDetails.super.isEnabled();
  }

  @Override
  public @Nullable String getPassword() {
    return passwordHash;
  }
}
