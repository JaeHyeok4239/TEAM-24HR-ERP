package com.hr24.employee.entity;

import com.hr24.global.entity.BaseTimeEntity;

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
@Table(name = "positions")
public class Position extends BaseTimeEntity {
	
    @Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "position_generator"
	)
	@SequenceGenerator(
	    name = "position_generator",
	    sequenceName = "position_seq",
	    allocationSize = 1
	)
    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "position_code", nullable = false, length = 50, unique = true)
    private String positionCode;

    @Column(name = "position_name", nullable = false, length = 100)
    private String positionName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false, length = 1)
    private String isActive = "Y";
    
    public static Position create(
            String positionCode,
            String positionName,
            String description,
            Integer sortOrder
    ) {
        Position position = new Position();
        position.positionCode = positionCode;
        position.positionName = positionName;
        position.description = description;
        position.sortOrder = sortOrder;
        position.isActive = "Y";
        return position;
    }

    public void update(
            String positionName,
            String description,
            Integer sortOrder,
            String isActive
    ) {
        this.positionName = positionName;
        this.description = description;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }

}
