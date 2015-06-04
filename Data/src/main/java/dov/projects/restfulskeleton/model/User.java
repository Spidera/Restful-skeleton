package dov.projects.restfulskeleton.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "given_name", nullable = false, length = 50)
    private String givenName;

    @Column(name = "family_name", length = 50)
    private String familyName;

    @Column(name = "email_address", unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 10)
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String name) {
        this.givenName = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String lastName) {
        this.familyName = lastName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (!givenName.equals(user.givenName)) return false;
        if (familyName != null ? !familyName.equals(user.familyName) : user.familyName != null) return false;
        if (!email.equals(user.email)) return false;
        return password.equals(user.password);

    }

    @Override
    public int hashCode() {
        int result = givenName.hashCode();
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }
}