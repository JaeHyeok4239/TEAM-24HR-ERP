package com.hr24.approval.dto;

import lombok.Getter;


public class ApprovalRequestDto {
	
	public enum ApprovalAction { APR , REJ }
	
	@Getter
	public static class ApprovalProcessDto {
		private ApprovalAction action;
		private String comment;
	}
}
