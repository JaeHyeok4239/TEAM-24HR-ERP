package com.hr24.document.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

public class HrRequestDto {
	
		//휴가 신청
		@Getter
		@Setter
		public static class LeaveDto {
		    private Long leaveType;
		    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
		    private LocalDateTime startDate;
		    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
		    private LocalDateTime endDate;
		    private Double leaveCnt;
		}
		
		//근태 정정 신청
	    @Getter
	    @Setter
	    public static class AttendanceCorrectionDto {
	        @JsonFormat(pattern = "yyyy-MM-dd")
	        private LocalDate targetDate;
	        private String correctionType;
	        private String correctionReason;
	        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
		    private LocalDateTime beforeTime;
	        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
		    private LocalDateTime afterTime;
	    }
}
