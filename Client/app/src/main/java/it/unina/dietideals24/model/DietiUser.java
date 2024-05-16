package it.unina.dietideals24.model;

import java.util.List;
import java.util.Objects;

public class DietiUser {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String biography;
    private List<String> links;
    private String geographicalArea;
    private String profilePictureUrl;

    public DietiUser(Long id, String name, String surname, String email, String password, String biography, List<String> links, String geographicalArea, String profilePictureUrl) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.biography = biography;
        this.links = links;
        this.geographicalArea = geographicalArea;
        this.profilePictureUrl = profilePictureUrl;
    }

    public DietiUser(Long id, String name, String surname, String email, String biography, List<String> links, String geographicalArea, String profilePictureUrl) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.biography = biography;
        this.links = links;
        this.geographicalArea = geographicalArea;
        this.profilePictureUrl = profilePictureUrl;
    }

    public DietiUser(String name, String surname, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }

    public DietiUser(String name, String surname, String email, String password, String biography, List<String> links, String geographicalArea, String profilePictureUrl) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.biography = biography;
        this.links = links;
        this.geographicalArea = geographicalArea;
        this.profilePictureUrl = profilePictureUrl;
    }

    public DietiUser(Long id, String name, String surname, String email, String biography, List<String> links, String geographicalArea) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.biography = biography;
        this.links = links;
        this.geographicalArea = geographicalArea;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public String getGeographicalArea() {
        return geographicalArea;
    }

    public void setGeographicalArea(String geographicalArea) {
        this.geographicalArea = geographicalArea;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DietiUser dietiUser = (DietiUser) o;
        return Objects.equals(id, dietiUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
