package com.hr24.work.mail.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mail_attachment")
public class MailAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mail_attachment_generator")
    @SequenceGenerator(name = "mail_attachment_generator", sequenceName = "mail_attachment_seq", allocationSize = 1)
    @Column(name = "attachment_id")
    private Long attachmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mail_id")
    private Mail mail;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "saved_name")
    private String savedName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "create_at")
    private LocalDateTime createAt;
}
