package com.hr24.work.schedule.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.work.schedule.entity.Holiday;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findByHolidayYear(Integer holidayYear);

    Optional<Holiday> findByHolidayDate(LocalDate holidayDate);
}
