export const documentSchemas = {
  leave: {
    leaveType: { label: "휴가 종류" },
    leaveDates: { label: "휴가 날짜" },
    leaveReason: { label: "신청 사유" },
  },
  attendance_correction: {
    correctionTarget: { label: "변경할 근태 이력" },
    correctionType: { label: "정정 유형(IN/OUT)" },
    beforeTime : { label : "변경 전 시간"},
    afterTime : { label : "변경 후 시간"}
  },
};