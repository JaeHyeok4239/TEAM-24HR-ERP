package com.hr24.document.entity;

import java.time.LocalDate;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "leave_date")
public class LeaveDate {

    @Id
    @Column(name = "leave_date_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_date_seq")
    @SequenceGenerator(name = "leave_date_seq", sequenceName = "leave_date_seq", allocationSize = 1)
    private Long leaveDateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_id")
    private Leave leave;

    @Column(name = "leave_date")
    private LocalDate leaveDate;
}