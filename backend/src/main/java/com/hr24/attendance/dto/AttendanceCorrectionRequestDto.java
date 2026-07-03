package com.hr24.attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hr24.attendance.enums.WorkplaceCode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

//관리자 정정(정규직/일용직) dto
public class AttendanceCorrectionRequestDto{ 

	//일용직 정정 정보 요청 dto
	@Getter
	@Setter
	public static class DailyLogDto {
	 private Long logId;
	 private LocalDateTime checkInTime;
	 private LocalDateTime checkOutTime;
	 
	 private WorkplaceCode workLocation; // 근무지(TEMP01 TEMP02 드롭다운)
	}
	
	//정규직 정정 정보 요청 dto
	@Getter
	@Setter
	public static class AttendanceResultDto {
	 private Long resultId;
	 private LocalDateTime checkInTime;
	 private LocalDateTime checkOutTime;
	}
	
	//정정 기록 정보 요청 dto
	@Getter
	@Setter
	  public static class CorrectionDto {
	   
	    private Long correctionTarget; // 어떤 근태 로그
	    private String correctionType; // IN/OUT
	    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	    private LocalDateTime beforeTime; // 수정 전 시간
	    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	    private LocalDateTime afterTime; // 수정 후 시간
	    private String correctionReason; // 정정 사유
	    
	    // 관리자 정보(이력 남기기용)
	    @Schema(hidden = true)
	    private String adminDepartment; // 관리자 부서
	    @Schema(hidden = true)
	    private String adminPosition; // 관리자 직급
	    
	    @Schema(hidden = true)
	    private LocalDate correctionDate; // 수정한 날짜
	}

}