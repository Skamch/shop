package com.internship.site.entity.user;

import com.internship.site.entity.Product;
import com.internship.site.entity.enums.Role;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table (name = "users", uniqueConstraints =
        {
                @UniqueConstraint(columnNames = "login")
        }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "purchases",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = null;

    private String name, login, password, email;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User() {
    }

    public User(String name, String login, String password, String email, Role role) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public List<Product> getProducts() { return products; }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setProduct(Product product) {
        this.products.add(product);
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
