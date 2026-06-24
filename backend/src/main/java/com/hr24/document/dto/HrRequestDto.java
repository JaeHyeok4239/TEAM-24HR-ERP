package com.hr24.document.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hr24.document.entity.Document;

import lombok.Getter;
import lombok.Setter;

public class HrRequestDto {
		
	
		//휴가 신청
		@Getter
		@Setter
		public static class LeaveDto {
		    private Long leaveType;
		    private List<LocalDate> leaveDates;
		    private BigDecimal leaveCnt;
		    private String leaveReason;
		}
		
		//근태 정정 신청
	    @Getter
	    @Setter
	    public static class AttendanceCorrectionDto {
	        private Long correctionTarget;
	        private String correctionType;
	        private String correctionReason;
	        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
		    private LocalDateTime beforeTime;
	        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
		    private LocalDateTime afterTime;
	    }
}
