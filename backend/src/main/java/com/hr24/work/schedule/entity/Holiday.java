package com.hr24.work.schedule.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "holidays")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "holidays_generator")
    @SequenceGenerator(name = "holidays_generator", sequenceName = "holidays_seq", allocationSize = 1)
    @Column(name = "holiday_id")
    private Long holidayId;

    @Column(name = "holiday_year")
    private Integer holidayYear;

    @Column(name = "holiday_date")
    private LocalDate holidayDate;

    @Column(name = "holiday_name")
    private String holidayName;

    @Column(name = "is_substitute")
    private int isSubstitute;
}
