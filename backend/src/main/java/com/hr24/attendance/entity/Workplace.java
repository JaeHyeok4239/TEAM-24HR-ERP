package com.hr24.attendance.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder                 
@NoArgsConstructor       
@AllArgsConstructor

@Entity
@Getter
@Table(name = "workplaces")
@SequenceGenerator(
		name="workplaces_seq",
		sequenceName = "workplaces_seq",
		initialValue = 1,
		allocationSize = 1
		)

public class Workplace{
	@Id
	@Column(name="workplace_id")
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "workplaces_seq")
	private Long workplaceId;
	
	@Column(name="workplace_code")
	private String workplaceCode;
	
	@Column(name="workplace_name")
	private String workplaceName;
	
	@Column(name="workplace_address")
	private String workplaceAddress;
	
	@Column(name="radius_meter")
	private Double radiusMeter;
	
	@Column(name="latitude")
	private Double latitude;
	
	@Column(name="longitude")
	private Double longitude;
	
	@Column(name="created_at")
	private LocalDateTime createdAt;
	
	@Column(name="updated_at")
	private LocalDateTime updatedAt;
	
}