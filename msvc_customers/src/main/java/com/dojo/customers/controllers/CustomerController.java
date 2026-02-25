package com.dojo.customers.controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dojo.customers.services.BlobStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dojo.customers.entities.Customer;
import com.dojo.customers.services.CustomerService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	private Logger logger = LoggerFactory.getLogger(CustomerController.class);
	private CustomerService service;
	private BlobStorageService blobStorageService;

	public CustomerController(CustomerService service,BlobStorageService blobStorageService) {
		this.service = service;
		this.blobStorageService = blobStorageService;
	}

	@RequestMapping(method =  RequestMethod.HEAD,path = "/{id}")
	public ResponseEntity<Void> existsById(@PathVariable("id") Long id) {
		if(service.exists(id)) {
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping
	public ResponseEntity<List<Customer>> findAll() {
		logger.info("Clientes  consultados : "+ Map.of("total", service.findAll().size()));
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("search")
	public ResponseEntity<List<Customer>> findByLikeUsername(@RequestParam String username) {
		logger.info("Clientes  consultados : "+ Map.of("total", service.findAll().size()));
		return ResponseEntity.ok(service.findByLikeUsername(username));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable Long id) {
		Optional<Customer> optionalCustomer =service.findById(id);
		if(optionalCustomer.isPresent()) {
			logger.info("Cliente : "+optionalCustomer.get().toString());
			return ResponseEntity.ok(optionalCustomer.get());
		}
			logger.warn("Cliente con id: "+id+" no encontrado!");
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Collections.singletonMap("Cliente","No encontrado"));
	}

	@GetMapping("/by-user/{username}")
	public ResponseEntity<?> findByUsername(@PathVariable String username) {
		Optional<Customer> optional=service.findByUsername(username);
		if(optional.isEmpty()) {
			logger.warn("Cliente con username: "+username+" no encontrado!");
			return new ResponseEntity<>(Collections.singletonMap("Cliente","No encontrado"), HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(optional.get());
	}

	//v1
	@PostMapping
	public ResponseEntity<Customer> save(@RequestBody Customer customer) {
		logger.info("Nuevo Cliente : "+ customer.toString());
		return ResponseEntity.ok(service.save(customer));
	}

	//v2
//	@PostMapping
//	public ResponseEntity<Customer> save(Customer customer, @RequestParam MultipartFile photo) throws IOException {
//		if (!photo.isEmpty()) {
//			customer.setPhoto(photo.getBytes());
//		}
//		return ResponseEntity.ok(customer);
//	}

//	@PostMapping("/upload")
//	public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
//		String url = blobStorageService.uploadFile(file);
//		return ResponseEntity.ok(url);
//	}

}