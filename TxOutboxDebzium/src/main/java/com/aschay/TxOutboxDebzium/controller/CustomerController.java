package com.aschay.TxOutboxDebzium.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.aschay.TxOutboxDebzium.domain.Customer;
import com.aschay.TxOutboxDebzium.dto.CustomerRequest;
import com.aschay.TxOutboxDebzium.exception.BadRequestException;
import com.aschay.TxOutboxDebzium.exception.RessourceNotFoundException;
import com.aschay.TxOutboxDebzium.service.CustomerService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.validation.Valid;

@RestController
public class CustomerController {

	@Autowired
	private CustomerService service;

	@GetMapping("/customers")
	public List<Customer> getAllCustomer() {
		return service.getAll();
	}

	@GetMapping("/customers/{id}")
	public Customer getOneCustomer(@Valid @PathVariable UUID id) {
		Optional<Customer> Customer = service.findbyId(id);
		if (!Customer.isPresent())
			throw new RessourceNotFoundException("Customer", "CustomerId", id);
		return Customer.get();
	}

	@PostMapping("/customers")
	public ResponseEntity<?> addNewCustomer(@Valid @RequestBody CustomerRequest newCustomer) throws BadRequestException, JsonProcessingException {
		UUID id=service.processAdd(newCustomer);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{Customerid}")
				.buildAndExpand(id).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@PutMapping("/customers/{id}")
	public ResponseEntity<?> EditCustomer(@Valid @RequestBody CustomerRequest newCustomer, @Valid @PathVariable UUID id) throws BadRequestException, JsonProcessingException {
		Optional<Customer> Customer = service.findbyId(id);
		if (!Customer.isPresent())
			throw new RessourceNotFoundException("Customer", "CustomerId", id);
		 service.processEdit(newCustomer,id);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(id).toUri();
		return ResponseEntity.ok().location(location).build();
	}

	@DeleteMapping("/customers/{id}")
	public ResponseEntity<?> deleteOneCustomer(@Valid @PathVariable UUID id) {
		Optional<Customer> Customer = service.getByCustomerCode(id);
		if (!Customer.isPresent())
			throw new RessourceNotFoundException("Customer", "CustomerId", id);
		service.processDelete(id);
		return ResponseEntity.noContent().build();
	}

}
