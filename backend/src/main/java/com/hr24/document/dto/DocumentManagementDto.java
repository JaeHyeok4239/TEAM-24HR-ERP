package com.hr24.document.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//문서 관리에 필요한 Dto(request/response 통합)
public class DocumentManagementDto {

	// 문서 유형 생성 시 요청 dto
	@Getter
	public static class DocumentTypeRequestDto {
		private String typeName;
		private String detailTable;
		private String requiredProcessing;
	}

	// 문서 유형별 스키마(양식) 생성/수정 요청 dto
	@Getter
	@NoArgsConstructor
	public static class DocumentTypeSchemaRequestDto {
		private List<FieldDto> fields;

		@Getter
		@NoArgsConstructor
		public static class FieldDto {
			private String name; // 필드명 (key)
			private String type; // string / number / date / datetime / date_list
			private boolean required; // 필수 여부
			private List<String> options; // 선택지 (SELECT/RADIO 등 필요한 경우만, 없으면 빈 리스트)
		}
	}

}
