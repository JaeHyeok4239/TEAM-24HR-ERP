package com.hr24.work.meeting.entity;

import com.hr24.employee.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reservation_participant")
public class ReservationParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_participant_generator")
    @SequenceGenerator(name = "reservation_participant_generator", sequenceName = "reservation_participant_seq", allocationSize = 1)
    @Column(name = "participant_id")
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private RoomReservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_organizer")
    private int isOrganizer;
}
