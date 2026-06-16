package com.hr24.document.dto;

import java.time.LocalDate;

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
		    private LocalDate startDate;
		    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
		    private LocalDate endDate;
		    private Double leaveCnt;
		}
		
	    // 나중에 근태정정 등 추가
//	    @Getter
//	    @Setter
//	    public static class AttendanceFixDto {
//	        ...
//	    }
}
