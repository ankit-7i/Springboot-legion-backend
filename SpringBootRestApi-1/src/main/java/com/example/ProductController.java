package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController
{
	@PostMapping("/saveproduct")
	public String createProduct(@RequestBody Product product) 
	{
		System.out.println(product);
		return "Product saved Successfully";
	}
	
	@GetMapping("/getProduct/{pid}")
	public Product getByProduct(@PathVariable int pid) {
		Product product = null;
		if(pid == 101) {
			product = new Product(101,"Laptop",40000.00);
		}
		else if (pid == 102) {
			product = new Product(102,"Mobile",23000.00);
		}
		return product;
	}
}
