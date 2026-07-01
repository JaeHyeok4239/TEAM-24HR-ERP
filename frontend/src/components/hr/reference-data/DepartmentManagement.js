"use client";

import { useEffect, useState } from "react";
import { Building2, Pencil, Plus } from "lucide-react";

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

  departments.forEach((department) => {
    departmentMap.set(department.departmentId, {
      ...department,
      children: [],
    });
  });

  departmentMap.forEach((department) => {
    const parentDepartment = departmentMap.get(department.parentDepartmentId);

    if (parentDepartment) {
      parentDepartment.children.push(department);
    } else {
      rootDepartments.push(department);
    }
  });

  const sortDepartments = (items) => {
    items.sort((first, second) => first.departmentId - second.departmentId);

    items.forEach((item) => {
      sortDepartments(item.children);
    });
  };

  sortDepartments(rootDepartments);

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

  const activeDepartmentCount = departments.filter(
    (department) => department.active,
  ).length;

  const inactiveDepartmentCount = departments.length - activeDepartmentCount;

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
      const message =
        DEPARTMENT_ERROR_MESSAGES[error.code] ||
        error.message ||
        "부서 정보를 저장하지 못했습니다.";

      const shouldCloseModal = CLOSE_MODAL_ERROR_CODES.includes(error.code);

      if (shouldCloseModal) {
        setModalMode(null);
        setModalError("");
        setForm(createInitialForm());

        setNotice({
          type: "error",
          message,
        });
      } else {
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
      <section className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
        <div className="border-b border-slate-200 px-5 py-4">
          <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <div className="flex min-w-0 items-start gap-3">
              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-[#eef7ff] text-[#1a2f4e]">
                <Building2 size={21} strokeWidth={1.8} />
              </div>

              <div className="min-w-0">
                <h2 className="text-base font-bold text-slate-950">
                  부서 관리
                </h2>

                <p className="mt-1 text-xs leading-5 text-slate-500">
                  조직의 부서 구조와 사용 여부를 관리합니다.
                </p>
              </div>
            </div>

            <div className="flex shrink-0 gap-2">
              <button
                type="button"
                onClick={handleOpenUpdateModal}
                disabled={!selectedDepartment}
                className="inline-flex h-9 items-center gap-1.5 rounded-md border border-slate-300 bg-white px-3 text-sm font-medium text-slate-700 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
              >
                <Pencil size={15} />
                수정
              </button>

              <button
                type="button"
                onClick={handleOpenCreateModal}
                className="inline-flex h-9 items-center gap-1.5 rounded-md bg-[#1a2f4e] px-3 text-sm font-semibold text-white hover:bg-[#25476e]"
              >
                <Plus size={16} />
                부서 추가
              </button>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-3 border-b border-slate-100 bg-slate-50/70">
          <SummaryItem label="전체" value={departments.length} />
          <SummaryItem label="사용" value={activeDepartmentCount} />
          <SummaryItem label="미사용" value={inactiveDepartmentCount} />
        </div>

        <div className="px-5 py-4">
          {notice.message && <Notice notice={notice} />}

          <div className="overflow-hidden rounded-md border border-slate-200">
            <table className="w-full border-collapse text-sm">
              <thead className="bg-slate-50 text-slate-500">
                <tr>
                  <th className="border-b border-slate-200 px-4 py-3 text-left text-xs font-semibold">
                    부서 구조
                  </th>
                  <th className="w-[150px] border-b border-slate-200 px-4 py-3 text-left text-xs font-semibold">
                    부서 코드
                  </th>
                  <th className="w-[110px] border-b border-slate-200 px-4 py-3 text-center text-xs font-semibold">
                    상태
                  </th>
                </tr>
              </thead>

              <tbody className="divide-y divide-slate-100">
                {isLoading && (
                  <EmptyRow
                    colSpan={3}
                    message="부서 목록을 불러오는 중입니다."
                  />
                )}

                {!isLoading && departments.length === 0 && (
                  <EmptyRow colSpan={3} message="등록된 부서가 없습니다." />
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
                        className={[
                          "cursor-pointer transition-colors",
                          isSelected ? "bg-[#eef7ff]" : "hover:bg-slate-50",
                        ].join(" ")}
                      >
                        <td
                          className={[
                            "border-l-4 px-4 py-3",
                            isSelected
                              ? "border-[#1a2f4e]"
                              : "border-transparent",
                          ].join(" ")}
                        >
                          <div
                            className="flex min-w-0 items-center"
                            style={{
                              paddingLeft: `${department.depth * 22}px`,
                            }}
                          >
                            {department.depth > 0 && (
                              <span className="mr-2 h-px w-4 shrink-0 bg-slate-300" />
                            )}

                            <span
                              className={[
                                "truncate",
                                department.depth === 0
                                  ? "font-semibold"
                                  : "font-medium",
                                department.active
                                  ? "text-slate-900"
                                  : "text-slate-400",
                              ].join(" ")}
                            >
                              {department.departmentName}
                            </span>
                          </div>
                        </td>

                        <td className="px-4 py-3">
                          <CodeBadge>{department.departmentCode}</CodeBadge>
                        </td>

                        <td className="px-4 py-3 text-center">
                          <StatusBadge active={department.active} />
                        </td>
                      </tr>
                    );
                  })}
              </tbody>
            </table>
          </div>

          <p className="mt-3 text-xs text-slate-400">
            항목을 선택한 뒤 수정 버튼을 눌러 부서명, 상위 부서, 사용 여부를
            변경할 수 있습니다.
          </p>
        </div>
      </section>

      {modalMode && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
          <form
            onSubmit={handleSubmit}
            noValidate
            className="w-full max-w-md overflow-hidden rounded-lg bg-white shadow-xl"
          >
            <div className="border-b border-slate-200 px-6 py-5">
              <h3 className="text-lg font-bold text-slate-950">
                {modalMode === "create" ? "부서 추가" : "부서 수정"}
              </h3>

              <p className="mt-1 text-sm text-slate-500">
                부서 기준정보를 입력해주세요.
              </p>
            </div>

            <div className="px-6 py-5">
              {modalError && <ModalError message={modalError} />}

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
                    className={[
                      inputClassName,
                      modalMode === "update"
                        ? "bg-slate-100 text-slate-500"
                        : "bg-white",
                    ].join(" ")}
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
                    className={inputClassName}
                  />
                </FormField>

                <FormField label="상위 부서">
                  <select
                    name="parentDepartmentId"
                    value={form.parentDepartmentId}
                    onChange={handleChange}
                    className={inputClassName}
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
                    className={inputClassName}
                  >
                    <option value="true">사용</option>
                    <option value="false">미사용</option>
                  </select>
                </FormField>
              </div>
            </div>

            <div className="flex justify-end gap-2 border-t border-slate-200 bg-slate-50 px-6 py-4">
              <button
                type="button"
                onClick={handleCloseModal}
                disabled={isSaving}
                className="h-9 rounded-md border border-slate-300 bg-white px-5 text-sm text-slate-700 hover:bg-slate-100 disabled:cursor-not-allowed disabled:opacity-50"
              >
                취소
              </button>

              <button
                type="submit"
                disabled={isSaving}
                className="h-9 rounded-md bg-[#1a2f4e] px-6 text-sm font-semibold text-white hover:bg-[#25476e] disabled:cursor-not-allowed disabled:bg-slate-300 disabled:text-slate-500"
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

const inputClassName =
  "h-9 w-full rounded-md border border-slate-200 px-3 text-sm outline-none focus:border-[#1a2f4e] focus:ring-2 focus:ring-[#a7f3ff]/40 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-500";

function SummaryItem({ label, value }) {
  return (
    <div className="border-r border-slate-100 px-5 py-3 last:border-r-0">
      <p className="text-xs font-medium text-slate-400">{label}</p>
      <p className="mt-1 text-lg font-bold text-slate-900">{value}</p>
    </div>
  );
}

function Notice({ notice }) {
  const isSuccess = notice.type === "success";

  return (
    <div
      role="alert"
      aria-live="polite"
      className={[
        "mb-4 rounded-md border px-4 py-3 text-sm font-medium",
        isSuccess
          ? "border-emerald-200 bg-emerald-50 text-emerald-700"
          : "border-red-200 bg-red-50 text-red-700",
      ].join(" ")}
    >
      {notice.message}
    </div>
  );
}

function EmptyRow({ colSpan, message }) {
  return (
    <tr>
      <td colSpan={colSpan} className="px-4 py-10 text-center text-slate-500">
        {message}
      </td>
    </tr>
  );
}

function CodeBadge({ children }) {
  return (
    <span className="inline-flex rounded-md bg-slate-100 px-2.5 py-1 text-xs font-semibold text-slate-600">
      {children || "-"}
    </span>
  );
}

function StatusBadge({ active }) {
  return (
    <span
      className={[
        "inline-flex rounded-full px-2.5 py-1 text-xs font-semibold",
        active
          ? "bg-emerald-50 text-emerald-700"
          : "bg-slate-100 text-slate-500",
      ].join(" ")}
    >
      {active ? "사용" : "미사용"}
    </span>
  );
}

function ModalError({ message }) {
  return (
    <div
      role="alert"
      aria-live="polite"
      className="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700"
    >
      {message}
    </div>
  );
}

function FormField({ label, children }) {
  return (
    <label className="block">
      <span className="mb-2 block text-sm font-semibold text-slate-700">
        {label}
      </span>

      {children}
    </label>
  );
}
