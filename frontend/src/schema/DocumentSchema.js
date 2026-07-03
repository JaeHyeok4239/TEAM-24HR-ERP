export const documentSchemas = {
  leave: {
    leaveTypeName: { label: "휴가 종류" },
    leaveDates: { label: "휴가 날짜" },
    leaveReason: { label: "신청 사유" },
  },
  attendance_correction: {
    targetDate : {label : "정정 대상 날짜"},
    correctionType: { label: "정정 유형(IN/OUT)" },
    correctionReason : { label : "정정 이유" },
    beforeTime : { label : "변경 전 시간"},
    afterTime : { label : "변경 후 시간"}
  },
};