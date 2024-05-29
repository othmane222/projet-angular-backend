package com.boostmytool.beststore.controllers;


import com.boostmytool.beststore.models.Product;
import com.boostmytool.beststore.models.ProductDto;
import com.boostmytool.beststore.services.ProductsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.*;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequestMapping("/products")
@CrossOrigin("*")
public class ProductsController {
    @Autowired
    private ProductsRepository repo;

    @GetMapping({"","/"})
    public String showProductList(Model model) {
        List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("products",products);
        return "products/index";
    }
    @GetMapping("/ps")
    public List<Product> productsss() {
        List<Product> products = repo.findAll();
        return products;
    }
    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto",productDto);
        return "products/CreateProduct";
    }
    @PostMapping("/create")
    public void createProduct(@RequestBody ProductDto productDto) {
        System.out.println("hiiii😊😊😊😊😊😊😊 " + productDto.toString());

        String imagePath = productDto.getImagePath();
        if (imagePath == null || imagePath.isEmpty()) {
            // Handle the error if the image path is required
        }

        // save image file
        Date createdAt = new Date();
        String storageFileName = String.valueOf(Paths.get(imagePath));


        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImagePath(storageFileName);
        product.setQuantity(productDto.getQuantity());
        repo.save(product);

    }
    @GetMapping("/edit")
    public String showEditPage(
            Model model,
            @RequestParam int id
    ) {
        try {
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);
            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());
            productDto.setQuantity(product.getQuantity());
            model.addAttribute("productDto", productDto);
        } catch (Exception ex) {
            System.out.println("Exception: + ex.getMessage()");
            return "redirect:/products";
        }
        return "products/EditProduct";
    }


    @PostMapping("/edit")
    public String updateProduct (
            Model model,
            @RequestParam int id,
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result
    ) {
        try {
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);
            if (result.hasErrors()) {
                return "products/EditProduct";
            }

            if (!productDto.getImagePath().isEmpty()) {
// delete old image
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImagePath());
                try {
                    Files.delete(oldImagePath);
                } catch (Exception ex) {
                    System.out.println("Exception: + ex.getMessage()");
                }
// save new image file
                Date createdAt = new Date();

                product.setImagePath(productDto.getImagePath());

            }
            product.setName (productDto.getName());
            product.setBrand (productDto.getBrand());
            product.setCategory (productDto.getCategory());
            product.setPrice (productDto.getPrice());
            product.setDescription (productDto.getDescription());
            product.setQuantity (productDto.getQuantity());

            repo.save (product);

        } catch (Exception ex) {
            System.out.println("Exception: + ex.getMessage()");
        }
        return "redirect:/products";

    }



    @GetMapping("/delete")
    public String deleteProducts (
            @RequestParam int id) {

        try {
            Product product = repo.findById(id).get();

            // delete product image
            Path imagePath = Paths.get("public/images/" + product.getImagePath());
            try {
                Files.delete(imagePath);
            } catch (Exception ex) {
                System.out.println("Exception: + ex.getMessage()");
            }
            //delete product
            repo.delete (product);
        }



        catch (Exception ex) {
            System.out.println("Exception: + ex.getMessage()");
        }
        return "redirect:/products";
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable int id) {
        Optional<Product> product = repo.findById(id);
        if (product.isPresent()) {
            repo.deleteById(id);
            return "Product deleted successfully";
        } else {
            return "Product not found";
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id, @RequestBody Product updatedProduct) {
        Optional<Product> productOptional = repo.findById(id);
        if (productOptional.isPresent()) {
            Product existingProduct = productOptional.get();
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setBrand(updatedProduct.getBrand());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setImagePath(updatedProduct.getImagePath());
            existingProduct.setQuantity(updatedProduct.getQuantity());

            // Set other fields as needed

            Product savedProduct = repo.save(existingProduct);
            return new ResponseEntity<>(savedProduct, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    }



