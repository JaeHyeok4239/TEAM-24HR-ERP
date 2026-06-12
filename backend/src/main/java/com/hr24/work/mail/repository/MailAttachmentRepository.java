package com.hr24.work.mail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.work.mail.entity.Mail;
import com.hr24.work.mail.entity.MailAttachment;

public interface MailAttachmentRepository extends JpaRepository<MailAttachment, Long> {

    List<MailAttachment> findByMail(Mail mail);
}
