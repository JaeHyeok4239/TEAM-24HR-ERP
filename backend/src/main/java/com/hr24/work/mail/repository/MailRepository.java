package com.hr24.work.mail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.employee.entity.User;
import com.hr24.work.mail.entity.Mail;

public interface MailRepository extends JpaRepository<Mail, Long> {

    List<Mail> findByUser(User user);
}
