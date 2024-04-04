package com.aschay.TxOutboxDebzium.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aschay.TxOutboxDebzium.domain.Customer;
import com.aschay.TxOutboxDebzium.domain.Outbox;
import com.aschay.TxOutboxDebzium.dto.CustomerRequest;
import com.aschay.TxOutboxDebzium.repository.CustomerRepository;
import com.aschay.TxOutboxDebzium.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private OutboxRepository outboxRepo;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Value("${app.outbox.agregatetype}") String aggrOutbox;

	@Transactional
	public UUID processAdd(CustomerRequest request) throws  JsonProcessingException {
		UUID customer_id = UUID.randomUUID();
		Customer c = new Customer();
		c.setId(customer_id);
		c.setEmail(request.getEmail());
		c.setUsername(request.getUsername());
		customerRepo.save(c);
		String payload = objectMapper.writeValueAsString(c);
		Outbox outboxEvent = new Outbox();
		outboxEvent.setId(UUID.randomUUID());
		outboxEvent.setAggregateid(customer_id.toString());
		outboxEvent.setType("CustomerCreated");
		outboxEvent.setPayload(payload);
		outboxEvent.setAgregatetype(aggrOutbox);
		outboxRepo.save(outboxEvent);
		log.info("\n\n\n\t\tCustomer created with id " + c.getId() + " - \t\t and Outbox entity created with Id: {}", outboxEvent.getId());
		outboxRepo.delete(outboxEvent);
		return c.getId();
		
	}

	public List<Customer> getAll() {
		return customerRepo.findAll();
	}

	public Optional<Customer> findbyId(UUID id) {
		return customerRepo.findById(id);
	}


	
	@Transactional
	public void processEdit(CustomerRequest request,UUID id ) throws  JsonProcessingException {
		Optional<Customer> cToUpdate = customerRepo.findById(id);
		if(cToUpdate.isPresent()) {
		Customer c = cToUpdate.get();
		c.setEmail(request.getEmail());
		c.setUsername(request.getUsername());
		customerRepo.save(c);
		String payload = objectMapper.writeValueAsString(c);
		Outbox outboxEvent = new Outbox();
		outboxEvent.setId(UUID.randomUUID());
		outboxEvent.setAggregateid(c.getId().toString());
		outboxEvent.setType("CustomerUpdated");
		outboxEvent.setPayload(payload);
		outboxEvent.setAgregatetype(aggrOutbox);
		outboxRepo.save(outboxEvent);
		log.info("\n\n\n\t\tCustomer updated with id " + c.getId() + " - \t\t and Outbox entity created with Id: {}", outboxEvent.getId());
		outboxRepo.delete(outboxEvent);
		}
	}
	
	
	@Transactional
	public void processDelete(UUID id) {
		customerRepo.deleteById(id);
		//String payload = objectMapper.writeValueAsString(c); I think delete should return null; or empty
		String payload ="";
		Outbox outboxEvent = new Outbox();
		outboxEvent.setId(UUID.randomUUID());
		outboxEvent.setAggregateid(id.toString());
		outboxEvent.setType("CustomerDeleted");
		outboxEvent.setPayload(payload);
		outboxEvent.setAgregatetype(aggrOutbox);
		outboxRepo.save(outboxEvent);
		log.info("\n\n\n\t\tCustomer Deleted with id " + id + " - \t\t and Outbox entity created with Id: {}", outboxEvent.getId());
		outboxRepo.delete(outboxEvent);
	}

	public Optional<Customer> getByCustomerCode(@Valid UUID id) {
		return customerRepo.findById(id);
	}

//	public void replicateData(Map<String, Object> customerData, Operation operation) {
//		ObjectMapper mapper = new ObjectMapper();
//		Customer customer = mapper.convertValue(customerData, Customer.class);
//
//		if (Operation.DELETE == operation) {
//			repo.deleteById(customer.getId());
//		} else {
//			repo.save(customer);
//		}
//	}

}