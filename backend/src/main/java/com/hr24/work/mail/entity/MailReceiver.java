package com.hr24.work.mail.entity;

import com.hr24.employee.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mail_receiver")
public class MailReceiver {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mail_receiver_generator")
    @SequenceGenerator(name = "mail_receiver_generator", sequenceName = "mail_receiver_seq", allocationSize = 1)
    @Column(name = "receiver_id")
    private Long receiverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mail_id")
    private Mail mail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_read")
    private int isRead;

    @Column(name = "is_deleted")
    private int isDeleted;
}
