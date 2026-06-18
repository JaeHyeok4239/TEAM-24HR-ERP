"use client";

import { useEffect, useState } from "react";

import {
  createDepartmentRequest,
  getDepartmentsRequest,
  updateDepartmentRequest,
} from "@/services/referenceDataService";

const createInitialForm = () => ({
  departmentCode: "",
  departmentName: "",
  parentDepartmentId: "",
  active: true,
});

const createDepartmentTreeRows = (departments) => {
  const departmentMap = new Map();
  const rootDepartments = [];

  // 각 부서에 children 배열을 추가해서 Map에 저장
  departments.forEach((department) => {
    departmentMap.set(department.departmentId, {
      ...department,
      children: [],
    });
  });

  // 상위 부서가 있으면 부모의 children에 넣고,
  // 없으면 최상위 부서로 분류
  departmentMap.forEach((department) => {
    const parentDepartment = departmentMap.get(department.parentDepartmentId);

    if (parentDepartment) {
      parentDepartment.children.push(department);
    } else {
      rootDepartments.push(department);
    }
  });

  // 현재는 departmentId 순서로 정렬
  const sortDepartments = (items) => {
    items.sort((first, second) => first.departmentId - second.departmentId);

    items.forEach((item) => {
      sortDepartments(item.children);
    });
  };

  sortDepartments(rootDepartments);

  // 화면에서 map으로 출력할 수 있도록
  // 트리를 다시 한 줄짜리 배열로 변환
  const rows = [];

  const flattenDepartments = (items, depth = 0) => {
    items.forEach((item) => {
      rows.push({
        ...item,
        depth,
      });

      flattenDepartments(item.children, depth + 1);
    });
  };

  flattenDepartments(rootDepartments);

  return rows;
};

const createEmptyNotice = () => ({
  type: "",
  message: "",
});

const DEPARTMENT_ERROR_MESSAGES = {
  DUPLICATE_DEPARTMENT_CODE: "이미 사용 중인 부서 코드입니다.",

  DUPLICATE_DEPARTMENT_NAME: "이미 사용 중인 부서명입니다.",

  DEPARTMENT_HAS_ASSIGNED_EMPLOYEES:
    "해당 부서에 소속된 직원이 있어 미사용 처리할 수 없습니다. 직원의 부서를 먼저 변경해주세요.",

  DEPARTMENT_HAS_ACTIVE_CHILDREN:
    "사용 중인 하위 부서가 있어 미사용 처리할 수 없습니다. 하위 부서를 먼저 변경해주세요.",

  INACTIVE_PARENT_DEPARTMENT: "미사용 부서는 상위 부서로 지정할 수 없습니다.",

  INVALID_PARENT_DEPARTMENT: "올바르지 않은 상위 부서입니다.",

  DEPARTMENT_NOT_FOUND: "부서 정보를 찾을 수 없습니다.",
};

const CLOSE_MODAL_ERROR_CODES = [
  "DEPARTMENT_HAS_ASSIGNED_EMPLOYEES",
  "DEPARTMENT_HAS_ACTIVE_CHILDREN",
];

export default function DepartmentManagement() {
  const [departments, setDepartments] = useState([]);
  const [selectedDepartmentId, setSelectedDepartmentId] = useState(null);

  const [modalMode, setModalMode] = useState(null);
  const [form, setForm] = useState(createInitialForm);

  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);

  const [notice, setNotice] = useState(createEmptyNotice);
  const [modalError, setModalError] = useState("");

  const selectedDepartment = departments.find(
    (department) => department.departmentId === selectedDepartmentId,
  );

  const departmentTreeRows = createDepartmentTreeRows(departments);

  const refreshDepartments = async () => {
    const response = await getDepartmentsRequest();

    setDepartments(response);
  };

  useEffect(() => {
    let cancelled = false;

    getDepartmentsRequest()
      .then((response) => {
        if (!cancelled) {
          setDepartments(response);
        }
      })
      .catch((error) => {
        console.error("부서 목록 조회 실패:", error);

        if (!cancelled) {
          setNotice({
            type: "error",
            message: error.message || "부서 목록을 불러오지 못했습니다.",
          });
        }
      })
      .finally(() => {
        if (!cancelled) {
          setIsLoading(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, []);

  const handleSelectDepartment = (departmentId) => {
    setSelectedDepartmentId(departmentId);
    setNotice(createEmptyNotice());
  };

  const handleOpenCreateModal = () => {
    setForm(createInitialForm());
    setModalError("");
    setNotice(createEmptyNotice());
    setModalMode("create");
  };

  const handleOpenUpdateModal = () => {
    if (!selectedDepartment) {
      return;
    }

    setForm({
      departmentCode: selectedDepartment.departmentCode ?? "",
      departmentName: selectedDepartment.departmentName ?? "",
      parentDepartmentId: selectedDepartment.parentDepartmentId ?? "",
      active: selectedDepartment.active,
    });

    setModalError("");
    setNotice(createEmptyNotice());
    setModalMode("update");
  };

  const handleCloseModal = () => {
    if (isSaving) {
      return;
    }

    setModalMode(null);
    setModalError("");
    setForm(createInitialForm());
  };

  const handleChange = (event) => {
    const { name, value } = event.target;
    let nextValue = value;
    if (name === "departmentCode") {
      nextValue = value.toUpperCase();
    }
    if (name === "active") {
      nextValue = value === "true";
    }
    setForm((previous) => ({ ...previous, [name]: nextValue }));
    setModalError("");
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const departmentCode = form.departmentCode.trim();

    const departmentName = form.departmentName.trim();

    if (modalMode === "create" && !departmentCode) {
      setModalError("부서 코드를 입력해주세요.");
      return;
    }

    if (!departmentName) {
      setModalError("부서명을 입력해주세요.");
      return;
    }

    const parentDepartmentId =
      form.parentDepartmentId === "" ? null : Number(form.parentDepartmentId);

    const changingToInactive =
      modalMode === "update" && selectedDepartment?.active && !form.active;

    if (changingToInactive) {
      const confirmed = window.confirm(
        "선택한 부서를 미사용 처리하시겠습니까?\n" +
          "소속 직원 또는 사용 중인 하위 부서가 있으면 변경되지 않습니다.",
      );

      if (!confirmed) {
        return;
      }
    }

    try {
      setIsSaving(true);
      setModalError("");
      setNotice(createEmptyNotice());

      let savedDepartment;

      if (modalMode === "create") {
        savedDepartment = await createDepartmentRequest({
          departmentCode,
          departmentName,
          parentDepartmentId,
          active: form.active,
        });
      } else {
        savedDepartment = await updateDepartmentRequest(
          selectedDepartment.departmentId,
          {
            departmentName,
            parentDepartmentId,
            active: form.active,
          },
        );
      }

      await refreshDepartments();

      setSelectedDepartmentId(savedDepartment.departmentId);

      setNotice({
        type: "success",
        message:
          modalMode === "create"
            ? "부서가 등록되었습니다."
            : "부서 정보가 수정되었습니다.",
      });

      setModalMode(null);
      setModalError("");
      setForm(createInitialForm());
    } catch (error) {
      console.error("부서 저장 실패:", error);

      const message =
        DEPARTMENT_ERROR_MESSAGES[error.code] ||
        error.message ||
        "부서 정보를 저장하지 못했습니다.";

      const shouldCloseModal = CLOSE_MODAL_ERROR_CODES.includes(error.code);

      if (shouldCloseModal) {
        // 현재 모달에서 해결할 수 없는 오류
        setModalMode(null);
        setModalError("");
        setForm(createInitialForm());

        setNotice({
          type: "error",
          message,
        });
      } else {
        // 입력값을 수정하면 해결할 수 있는 오류
        setModalError(message);
      }
    } finally {
      setIsSaving(false);
    }
  };

  const availableParentDepartments = departmentTreeRows.filter((department) => {
    const isNotSelf =
      department.departmentId !== selectedDepartment?.departmentId;

    return department.active && isNotSelf;
  });

  return (
    <>
      <section className="rounded-md border bg-white p-5">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="font-semibold text-slate-900">부서 관리</h2>

          <div className="flex gap-2">
            <button
              type="button"
              onClick={handleOpenUpdateModal}
              disabled={!selectedDepartment}
              className="rounded-md border px-3 py-2 text-sm text-slate-600 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
            >
              수정
            </button>

            <button
              type="button"
              onClick={handleOpenCreateModal}
              className="rounded-md bg-blue-500 px-3 py-2 text-sm text-white hover:bg-blue-600"
            >
              부서 추가
            </button>
          </div>
        </div>

        {notice.message && (
          <div
            role="alert"
            aria-live="polite"
            className={`mb-4 rounded-md border px-4 py-3 text-sm ${
              notice.type === "success"
                ? "border-green-200 bg-green-50 text-green-700"
                : "border-red-200 bg-red-50 text-red-700"
            }`}
          >
            {notice.message}
          </div>
        )}

        <div className="overflow-hidden rounded-md border">
          <table className="w-full border-collapse text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="border-b px-4 py-3 text-left">부서 구조</th>
                <th className="border-b px-4 py-3 text-left">부서 코드</th>
                <th className="border-b px-4 py-3 text-center">사용 여부</th>
              </tr>
            </thead>

            <tbody>
              {isLoading && (
                <tr>
                  <td
                    colSpan={3}
                    className="px-4 py-8 text-center text-slate-500"
                  >
                    부서 목록을 불러오는 중입니다.
                  </td>
                </tr>
              )}

              {!isLoading && departments.length === 0 && (
                <tr>
                  <td
                    colSpan={3}
                    className="px-4 py-8 text-center text-slate-500"
                  >
                    등록된 부서가 없습니다.
                  </td>
                </tr>
              )}

              {!isLoading &&
                departmentTreeRows.map((department) => {
                  const isSelected =
                    department.departmentId === selectedDepartmentId;

                  return (
                    <tr
                      key={department.departmentId}
                      onClick={() =>
                        handleSelectDepartment(department.departmentId)
                      }
                      className={`cursor-pointer border-b last:border-b-0 ${
                        isSelected ? "bg-blue-50" : "hover:bg-slate-50"
                      }`}
                    >
                      <td
                        className={`px-4 py-3 ${
                          department.active
                            ? "text-slate-900"
                            : "text-slate-400"
                        }`}
                      >
                        <div
                          className="flex items-center"
                          style={{
                            paddingLeft: `${department.depth * 24}px`,
                          }}
                        >
                          {department.depth > 0 && (
                            <span className="mr-2 text-slate-400">└─</span>
                          )}

                          <span
                            className={
                              department.depth === 0 ? "font-semibold" : ""
                            }
                          >
                            {department.departmentName}
                          </span>
                        </div>
                      </td>
                      <td className="px-4 py-3 text-slate-500">
                        {department.departmentCode}
                      </td>

                      <td className="px-4 py-3 text-center">
                        <span
                          className={`rounded-full px-2 py-1 text-xs ${
                            department.active
                              ? "bg-green-100 text-green-700"
                              : "bg-slate-100 text-slate-500"
                          }`}
                        >
                          {department.active ? "사용" : "미사용"}
                        </span>
                      </td>
                    </tr>
                  );
                })}
            </tbody>
          </table>
        </div>
      </section>

      {modalMode && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
          <form
            onSubmit={handleSubmit}
            noValidate
            className="w-full max-w-md rounded-md bg-white p-6 shadow-lg"
          >
            <div className="mb-5 border-b pb-4">
              <h3 className="font-semibold text-slate-900">
                {modalMode === "create" ? "부서 추가" : "부서 수정"}
              </h3>
            </div>

            {modalError && (
              <div
                role="alert"
                aria-live="polite"
                className="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700"
              >
                {modalError}
              </div>
            )}

            <div className="space-y-4">
              <FormField label="부서 코드">
                <input
                  type="text"
                  name="departmentCode"
                  value={form.departmentCode}
                  onChange={handleChange}
                  readOnly={modalMode === "update"}
                  maxLength={50}
                  placeholder="예: SALES"
                  className={`w-full rounded-md border px-3 py-2 text-sm outline-none focus:border-blue-500 ${
                    modalMode === "update" ? "bg-slate-100 text-slate-500" : ""
                  }`}
                />
              </FormField>

              <FormField label="부서명">
                <input
                  type="text"
                  name="departmentName"
                  value={form.departmentName}
                  onChange={handleChange}
                  maxLength={100}
                  placeholder="부서명을 입력해주세요."
                  className="w-full rounded-md border px-3 py-2 text-sm outline-none focus:border-blue-500"
                />
              </FormField>

              <FormField label="상위 부서">
                <select
                  name="parentDepartmentId"
                  value={form.parentDepartmentId}
                  onChange={handleChange}
                  className="w-full rounded-md border px-3 py-2 text-sm outline-none focus:border-blue-500"
                >
                  <option value="">상위 부서 없음</option>

                  {availableParentDepartments.map((department) => (
                    <option
                      key={department.departmentId}
                      value={department.departmentId}
                    >
                      {"　".repeat(department.depth)}
                      {department.departmentName}
                    </option>
                  ))}
                </select>
              </FormField>

              <FormField label="사용 여부">
                <select
                  name="active"
                  value={String(form.active)}
                  onChange={handleChange}
                  className="w-full rounded-md border px-3 py-2 text-sm outline-none focus:border-blue-500"
                >
                  <option value="true">사용</option>

                  <option value="false">미사용</option>
                </select>
              </FormField>
            </div>

            <div className="mt-6 flex justify-end gap-2">
              <button
                type="button"
                onClick={handleCloseModal}
                disabled={isSaving}
                className="rounded-md bg-slate-100 px-4 py-2 text-sm text-slate-600 hover:bg-slate-200 disabled:cursor-not-allowed disabled:opacity-50"
              >
                취소
              </button>

              <button
                type="submit"
                disabled={isSaving}
                className="rounded-md bg-blue-500 px-4 py-2 text-sm text-white hover:bg-blue-600 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {isSaving ? "저장 중..." : "저장"}
              </button>
            </div>
          </form>
        </div>
      )}
    </>
  );
}

function FormField({ label, children }) {
  return (
    <label className="block">
      <span className="mb-1 block text-sm font-medium text-slate-700">
        {label}
      </span>

      {children}
    </label>
  );
}
