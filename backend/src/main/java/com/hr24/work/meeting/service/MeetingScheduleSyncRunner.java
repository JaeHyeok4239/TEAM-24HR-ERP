package com.hr24.work.meeting.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

// 앱 기동 시 아직 일정으로 동기화되지 않은 과거 회의실 예약을 자동으로 채워넣는다.
@Component
@RequiredArgsConstructor
public class MeetingScheduleSyncRunner implements CommandLineRunner {

    private final MeetingService meetingService;

    @Override
    public void run(String... args) {
        meetingService.syncMissingSchedules();
    }
}
