// 권한 이름을 한 곳에서 관리하기 위한 상수
// 백엔드에서 "ADMIN" 또는 "ROLE_ADMIN" 형태로 내려와도 비교할 수 있게 처리한다.
export const ROLES = Object.freeze({
  ADMIN: "ADMIN",
  USER: "USER",
  HR: "HR",
  ATTENDANCE: "ATTENDANCE",
  PAYROLL: "PAYROLL",
  HR_LEAD: "HR_LEAD",
});

// 근태관리 조회 권한
export const ATTENDANCE_VIEW_ROLES = [
  ROLES.ADMIN, 
  ROLES.ATTENDANCE
];

// 인사관리 직원 목록 조회 권한
// 직원 목록, 직원 상세 조회 가능
export const HR_VIEW_ROLES = [
  ROLES.ADMIN,
  ROLES.HR_LEAD,
  ROLES.HR,
];

// 인사관리 기준정보 관리 권한
// 부서/직급 같은 기준정보 수정 가능
export const HR_MANAGE_ROLES = [
  ROLES.ADMIN,
  ROLES.HR_LEAD,
];

export const DEFAULT_APR_ROLES = [
  ROLES.ADMIN,
  ROLES.HR_LEAD,
  ROLES.ATTENDANCE,
  ROLES.PAYROLL,
];

// 백엔드에서 ROLE_ADMIN 형태로 내려와도 ADMIN으로 비교하기 위해 정규화한다.
export const normalizeRole = (role) => {
  if (!role || typeof role !== "string") {
    return "";
  }

  return role.replace("ROLE_", "");
};

// 현재 사용자가 allowedRoles 중 하나라도 가지고 있는지 확인한다.
// allowedRoles가 없으면 권한 제한이 없는 메뉴로 판단한다.
export const hasAnyRole = (userRoles = [], allowedRoles) => {
  if (!allowedRoles || allowedRoles.length === 0) {
    return true;
  }

  const normalizedUserRoles = userRoles.map(normalizeRole);

  return allowedRoles.some((allowedRole) =>
    normalizedUserRoles.includes(normalizeRole(allowedRole)),
  );
};

// 메뉴 배열에서 현재 사용자 권한으로 볼 수 있는 메뉴만 남긴다.
// children이 있는 메뉴는 하위 메뉴도 권한 기준으로 필터링한다.
export const filterNavItemsByRole = (items, userRoles = []) => {
  return items
    .map((item) => {
      if (!hasAnyRole(userRoles, item.allowedRoles)) {
        return null;
      }

      if (!item.children) {
        return item;
      }

      const filteredChildren = item.children.filter((child) =>
        hasAnyRole(userRoles, child.allowedRoles),
      );

      if (filteredChildren.length === 0) {
        return null;
      }

      return {
        ...item,
        children: filteredChildren,
      };
    })
    .filter(Boolean);
};