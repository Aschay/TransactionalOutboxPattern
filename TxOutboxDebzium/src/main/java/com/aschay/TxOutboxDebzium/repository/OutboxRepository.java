package com.aschay.TxOutboxDebzium.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aschay.TxOutboxDebzium.domain.Outbox;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, UUID> {

}

