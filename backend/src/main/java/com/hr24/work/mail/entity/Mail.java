package com.hr24.work.mail.entity;

import java.time.LocalDateTime;

import com.hr24.employee.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mail")
public class Mail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mail_generator")
    @SequenceGenerator(name = "mail_generator", sequenceName = "mail_seq", allocationSize = 1)
    @Column(name = "mail_id")
    private Long mailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_mail_id")
    private Mail parentMail;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "create_at")
    private LocalDateTime createAt;
}
