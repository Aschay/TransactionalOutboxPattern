package com.aschay.TxOutboxDebzium.domain;

import java.sql.Timestamp;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Outbox {

	public static final int VARCHAR_MAX_LENGTH = 4096;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(java.sql.Types.VARCHAR)
	private UUID id;

	@Column(nullable = false, length = VARCHAR_MAX_LENGTH)
	private String payload;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@CreatedDate
	private Timestamp createdDate;



	@NotBlank
	private String agregatetype; 
	@NotBlank
	private String aggregateid;
	@NotBlank
	private String type;

}