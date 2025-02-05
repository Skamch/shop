package com.internship.site.controller;

import com.internship.site.entity.Country;
import com.internship.site.entity.Product;
import com.internship.site.entity.Type;
import com.internship.site.entity.enums.Role;
import com.internship.site.entity.user.User;
import com.internship.site.repository.CountryRepo;
import com.internship.site.repository.ProductRepo;
import com.internship.site.repository.TypeRepo;
import com.internship.site.repository.UserRepo;
import com.internship.site.service.MyUserDetailsService;
import com.internship.site.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;

@RestController
@RequestMapping("api/products")
public class ProductController {
    @Value("${resourcePath}")
    private String resourcePath;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    HttpServletRequest request;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TypeRepo typeRepo;

    @Autowired
    private CountryRepo countryRepo;

    @ResponseBody
    @GetMapping(value = "/get-img", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImg(@RequestParam(value = "id") int id) throws IOException {
        Product product = productRepo.findById(id);
        Path path = Paths.get(resourcePath + product.getImg());

        return Files.readAllBytes(path);
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable int id) {
        Product product = productRepo.findById(id);
        product.setType(new Type(product.getType().getName()));
        product.setCountry(new Country(product.getCountry().getName()));
        return product;
    }

    @PostMapping("/choose/{id}")
    public String chooseProduct(@PathVariable int id, @RequestBody User user) {
        try {
            userRepo.setProduct(id, userRepo.findByLogin(user.getLogin()).getId());
            return "{ \"status\": \"ok\" }";
        } catch (Exception err) {
            return "{ \"status\": \"Продукт уже есть в корзине\" }";
        }
    }

    @GetMapping("/get-all")
    public List<Product> getAllProducts(@RequestParam(value = "name") String name, @RequestParam(value = "type") String typeName, @RequestParam(value = "country") String countryName) {
        List<Product> products;

        Type type = typeRepo.findByName(typeName);
        Country country = countryRepo.findByName(countryName);
        name = "%" + name + "%";
        System.out.println(typeName);

        if (type != null) {
            if (country != null) {
                products = productRepo.findAllByNameLikeAndTypeAndCountry(name, type, country);
            } else {
                products = productRepo.findAllByNameLikeAndType(name, type);
            }
        } else {
            if (country != null) {
                products = productRepo.findAllByNameLikeAndCountry(name, country);
            } else {
               products = productRepo.findAllByNameLike(name);
            }
        }

        for (Product product : products) {
            product.setCountry(new Country(product.getCountry().getName()));
            product.setType(new Type(product.getType().getName()));
        }
        return products;
    }

    @PostMapping("/add-img")
    public void addImg(@RequestParam(value="img") MultipartFile image) throws IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = authorizationHeader.substring(7);
        String login = jwtTokenUtil.extractUsername(jwt);
        Role role = userRepo.findByLogin(login).getRole();

        if (role == Role.ROLE_ADMINISTRATOR || role == Role.ROLE_SUPER_ADMINISTRATOR) {
            Files.write(Paths.get(resourcePath + image.getOriginalFilename()), image.getBytes());
        }
    }

    @PostMapping("/add")
    public void addProduct(@RequestBody Product product) {
        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = authorizationHeader.substring(7);
        String login = jwtTokenUtil.extractUsername(jwt);

        Role role = userRepo.findByLogin(login).getRole();

        if (role == Role.ROLE_ADMINISTRATOR || role == Role.ROLE_SUPER_ADMINISTRATOR) {
            Type type = typeRepo.findByName(product.getType().getName());
            Country country = countryRepo.findByName(product.getCountry().getName());

            if (type == null) {
                type = typeRepo.save(product.getType());
            }

            if (country == null) {
                country = countryRepo.save(product.getCountry());
            }

            product.setType(type);
            product.setCountry(country);
            productRepo.save(product);
        }
    }

    @PostMapping("/delete")
    public void deleteProduct(@RequestBody int id) {
        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = authorizationHeader.substring(7);
        String login = jwtTokenUtil.extractUsername(jwt);

        Role role = userRepo.findByLogin(login).getRole();

        if (role == Role.ROLE_ADMINISTRATOR || role == Role.ROLE_SUPER_ADMINISTRATOR) {
            productRepo.delete(productRepo.findById(id));
        }
    }

    @GetMapping("/types")
    public List<Type> getTypes() {
        List<Type> types = typeRepo.findAll();

        for (Type type: types) {
            type.setProducts(null);
        }

        return types;
    }

    @GetMapping("/countries")
    public List<Country> getCountries() {
        List<Country> countries = countryRepo.findAll();

        for (Country country: countries) {
            country.setProducts(null);
        }

        return countries;
    }
}
