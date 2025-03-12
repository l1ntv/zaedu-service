package ru.tbank.zaedu.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user_statuses")
public class UserStatus {
    @Id
    @Column(name = "status_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "status")
    private List<User> users;

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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
