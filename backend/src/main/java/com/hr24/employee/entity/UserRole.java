package com.hr24.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
	name = "user_roles",
	uniqueConstraints = {
	    @UniqueConstraint(
	        name = "uk_user_roles_user_role",
	        columnNames = {
	            "employee_id",
	            "role_id"
	        }
	    )
	}
)	
public class UserRole {

	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "user_role_generator"
	)
	@SequenceGenerator(
	    name = "user_role_generator",
	    sequenceName = "user_role_seq",
	    allocationSize = 1
	)
    @Column(name = "user_roles_id")
    private Long userRolesId;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", nullable = false)
    private Role role;
}