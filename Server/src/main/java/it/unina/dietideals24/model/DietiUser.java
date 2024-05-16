package it.unina.dietideals24.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "unique_email", columnNames = "email"))
public class DietiUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    @Column(unique = true)
    private String email;
    private String password;
    private String biography;
    private List<String> links;
    private String geographicalArea;
    private String profilePictureUrl;

    public DietiUser(String name, String surname, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }

    public DietiUser(String name, String surname, String email, String biography, String geographicalArea, List<String> links) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.biography = biography;
        this.geographicalArea = geographicalArea;
        if (links == null)
            this.links = new ArrayList<>();
        else
            this.links = links;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DietiUser dietiUser = (DietiUser) o;
        return Objects.equals(id, dietiUser.id) && Objects.equals(name, dietiUser.name) && Objects.equals(surname, dietiUser.surname) && Objects.equals(email, dietiUser.email) && Objects.equals(biography, dietiUser.biography) && Objects.equals(links, dietiUser.links) && Objects.equals(geographicalArea, dietiUser.geographicalArea) && Objects.equals(profilePictureUrl, dietiUser.profilePictureUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, email, biography, links, geographicalArea, profilePictureUrl);
    }
}