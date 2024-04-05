package com.aschay.TxOutboxDebzium.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.aschay.TxOutboxDebzium.domain.Outbox;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
	@Modifying(clearAutomatically=true, flushAutomatically=true)
    void delete(Outbox o);
}


