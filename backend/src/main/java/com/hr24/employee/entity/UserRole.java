package com.hr24.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_roles")
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

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "role_id")
    private Long roleId;
}