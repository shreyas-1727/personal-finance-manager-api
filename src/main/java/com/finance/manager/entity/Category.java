package com.finance.manager.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories", uniqueConstraints = {
    // Ensures a specific user cannot have duplicate category names
    @UniqueConstraint(columnNames = {"name", "user_id"})
})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // Will store "INCOME" or "EXPENSE"

    @Column(nullable = false)
    private boolean isCustom;

    // A Category can belong to one User. If null, it's a default global category.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Category() {}

    public Category(String name, String type, boolean isCustom, User user) {
        this.name = name;
        this.type = type;
        this.isCustom = isCustom;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean getIsCustom() { return isCustom; }
    public void setIsCustom(boolean custom) { isCustom = custom; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}