package com.aschay.TxOutboxDebzium.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aschay.TxOutboxDebzium.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

	Optional<Customer> findById(UUID id);

	boolean existsById(UUID id);

}
