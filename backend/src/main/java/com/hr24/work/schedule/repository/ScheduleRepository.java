package com.hr24.work.schedule.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.User;
import com.hr24.work.schedule.entity.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByUser(User user);

    List<Schedule> findByScheduleType(String scheduleType);

    List<Schedule> findByDepartment(Department department);

    List<Schedule> findByStartDtLessThanEqualAndEndDtGreaterThanEqual(LocalDate endDt, LocalDate startDt);
}
