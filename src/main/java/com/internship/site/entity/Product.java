package com.internship.site.entity;

import com.internship.site.entity.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table (name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToMany(mappedBy = "products")
    private Set<User> users = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Type type;

    private String name, img;
    @Column(length=1000)
    private String description;
    private int cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    public Product() {
    }

    public Product(String name, String description, String img, int cost, Type type, Country country) {
        this.name = name;
        this.description = description;
        this.img = img;
        this.cost = cost;
        this.type = type;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }
}
