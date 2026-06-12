package com.hr24.work.meeting.entity;

import java.time.LocalDate;
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
@Table(name = "room_reservation")
public class RoomReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_reservation_generator")
    @SequenceGenerator(name = "room_reservation_generator", sequenceName = "room_reservation_seq", allocationSize = 1)
    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private MeetingRoom meetingRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title")
    private String title;

    @Column(name = "rsv_date")
    private LocalDate rsvDate;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "status")
    private String status;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "create_at")
    private LocalDateTime createAt;
}
