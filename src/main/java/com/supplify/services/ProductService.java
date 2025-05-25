package com.supplify.services;


import java.util.List;
import java.util.Optional;

import com.supplify.entity.Product;

public interface ProductService{
   
    Product getProductById(Long id);

}
