package com.hr24.work.mail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.employee.entity.User;
import com.hr24.work.mail.entity.Mail;
import com.hr24.work.mail.entity.MailReceiver;

public interface MailReceiverRepository extends JpaRepository<MailReceiver, Long> {

    List<MailReceiver> findByUserAndIsDeleted(User user, int isDeleted);

    List<MailReceiver> findByMail(Mail mail);
}
