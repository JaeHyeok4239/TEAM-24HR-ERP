"use client";

import { useEffect, useState } from "react";

import {
  createPositionRequest,
  getPositionsRequest,
  updatePositionRequest,
} from "@/services/referenceDataService";

const createInitialForm = () => ({
  positionCode: "",
  positionName: "",
  upperPositionId: "",
  isActive: "Y",
});

const createEmptyNotice = () => ({
  type: "",
  message: "",
});

const POSITION_ERROR_MESSAGES = {
  DUPLICATE_POSITION_CODE: "이미 사용 중인 직급 코드입니다.",
  DUPLICATE_POSITION_NAME: "이미 사용 중인 직급명입니다.",
  POSITION_HAS_ASSIGNED_EMPLOYEES:
    "해당 직급을 사용하는 직원이 있어 미사용 처리할 수 없습니다.\n직원의 직급을 먼저 변경해주세요.",
  POSITION_NOT_FOUND: "직급 정보를 찾을 수 없습니다.",
  INVALID_POSITION_ORDER: "직급 위치가 올바르지 않습니다.",
};

const CLOSE_MODAL_ERROR_CODES = [
  "POSITION_HAS_ASSIGNED_EMPLOYEES",
];

export default function PositionManagement() {
  const [positions, setPositions] = useState([]);
  const [selectedPositionId, setSelectedPositionId] = useState(null);
  const [modalMode, setModalMode] = useState(null);
  const [form, setForm] = useState(createInitialForm);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [notice, setNotice] = useState(createEmptyNotice);
  const [modalError, setModalError] = useState("");

  const selectedPosition = positions.find(
    (position) => position.positionId === selectedPositionId,
  );

  const sortedPositions = [...positions].sort((first, second) => {
    const firstActive = first.isActive === "Y";
    const secondActive = second.isActive === "Y";

    if (firstActive !== secondActive) {
      return firstActive ? -1 : 1;
    }

    if (firstActive && secondActive) {
      const firstSortOrder = first.sortOrder ?? 0;
      const secondSortOrder = second.sortOrder ?? 0;

      if (firstSortOrder !== secondSortOrder) {
        return secondSortOrder - firstSortOrder;
      }
    }

    return first.positionId - second.positionId;
  });

  const activePositionOptions = positions
    .filter((position) => position.isActive === "Y")
    .filter((position) => position.positionId !== selectedPosition?.positionId)
    .sort((first, second) => {
      const firstSortOrder = first.sortOrder ?? 0;
      const secondSortOrder = second.sortOrder ?? 0;

      if (firstSortOrder !== secondSortOrder) {
        return secondSortOrder - firstSortOrder;
      }

      return first.positionId - second.positionId;
    });

  const refreshPositions = async () => {
    const response = await getPositionsRequest();
    setPositions(response);

    return response;
  };

  useEffect(() => {
    let cancelled = false;

    getPositionsRequest()
      .then((response) => {
        if (!cancelled) {
          setPositions(response);
        }
      })
      .catch((error) => {
        console.error("직급 목록 조회 실패:", error);

        if (!cancelled) {
          setNotice({
            type: "error",
            message: error.message || "직급 목록을 불러오지 못했습니다.",
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

  const findUpperPositionId = (targetPosition) => {
    if (!targetPosition || targetPosition.isActive !== "Y") {
      return "";
    }

    const activePositionsAsc = positions
      .filter((position) => position.isActive === "Y")
      .sort((first, second) => {
        const firstSortOrder = first.sortOrder ?? 0;
        const secondSortOrder = second.sortOrder ?? 0;

        if (firstSortOrder !== secondSortOrder) {
          return firstSortOrder - secondSortOrder;
        }

        return first.positionId - second.positionId;
      });

    const targetIndex = activePositionsAsc.findIndex(
      (position) => position.positionId === targetPosition.positionId,
    );

    if (
      targetIndex < 0 ||
      targetIndex === activePositionsAsc.length - 1
    ) {
      return "";
    }

    return String(activePositionsAsc[targetIndex + 1].positionId);
  };

  const handleSelectPosition = (positionId) => {
    setSelectedPositionId(positionId);
    setNotice(createEmptyNotice());
  };

  const handleOpenCreateModal = () => {
    setForm(createInitialForm());
    setModalError("");
    setNotice(createEmptyNotice());
    setModalMode("create");
  };

  const handleOpenUpdateModal = () => {
    if (!selectedPosition) {
      return;
    }

    setForm({
      positionCode: selectedPosition.positionCode ?? "",
      positionName: selectedPosition.positionName ?? "",
      upperPositionId: findUpperPositionId(selectedPosition),
      isActive: selectedPosition.isActive ?? "Y",
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

    if (name === "positionCode") {
      nextValue = value.toUpperCase();
    }

    setForm((previous) => ({
      ...previous,
      [name]: nextValue,
    }));

    setModalError("");
  };

  const parseUpperPositionId = () => {
    if (form.isActive === "N") {
      return null;
    }

    if (form.upperPositionId === "") {
      return null;
    }

    const upperPositionId = Number(form.upperPositionId);

    if (Number.isNaN(upperPositionId)) {
      throw new Error("바로 위 직급을 올바르게 선택해주세요.");
    }

    return upperPositionId;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const positionCode = form.positionCode.trim().toUpperCase();
    const positionName = form.positionName.trim();

    if (modalMode === "create" && !positionCode) {
      setModalError("직급 코드를 입력해주세요.");
      return;
    }

    if (
      modalMode === "create" &&
      !/^[A-Z0-9_-]+$/.test(positionCode)
    ) {
      setModalError("직급 코드는 대문자, 숫자, _, -만 사용할 수 있습니다.");
      return;
    }

    if (!positionName) {
      setModalError("직급명을 입력해주세요.");
      return;
    }

    if (
      modalMode === "update" &&
      !["Y", "N"].includes(form.isActive)
    ) {
      setModalError("사용 여부는 Y 또는 N만 가능합니다.");
      return;
    }

    if (
      modalMode === "update" &&
      form.upperPositionId !== "" &&
      Number(form.upperPositionId) === selectedPosition?.positionId
    ) {
      setModalError("자기 자신을 바로 위 직급으로 선택할 수 없습니다.");
      return;
    }

    const changingToInactive =
      modalMode === "update" &&
      selectedPosition?.isActive === "Y" &&
      form.isActive === "N";

    if (changingToInactive) {
      const confirmed = window.confirm(
        "선택한 직급을 미사용 처리하시겠습니까?\n" +
          "해당 직급을 사용하는 직원이 있으면 변경되지 않습니다.",
      );

      if (!confirmed) {
        return;
      }
    }

    let upperPositionId;

    try {
      upperPositionId = parseUpperPositionId();
    } catch (error) {
      setModalError(error.message);
      return;
    }

    try {
      setIsSaving(true);
      setModalError("");
      setNotice(createEmptyNotice());

      let savedPosition;

      if (modalMode === "create") {
        savedPosition = await createPositionRequest({
          positionCode,
          positionName,
          upperPositionId,
        });
      } else {
        savedPosition = await updatePositionRequest(
          selectedPosition.positionId,
          {
            positionName,
            isActive: form.isActive,
            upperPositionId,
          },
        );
      }

      await refreshPositions();

      setSelectedPositionId(savedPosition.positionId);
      setNotice({
        type: "success",
        message:
          modalMode === "create"
            ? "직급이 등록되었습니다."
            : "직급 정보가 수정되었습니다.",
      });

      setModalMode(null);
      setModalError("");
      setForm(createInitialForm());
    } catch (error) {
      console.error("직급 저장 실패:", error);

      const message =
        POSITION_ERROR_MESSAGES[error.code] ||
        error.message ||
        "직급 정보를 저장하지 못했습니다.";

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

  return (
    <>
      <section className="rounded-md border bg-white p-5">
        <div className="flex items-center justify-between gap-3">
          <div>
            <h2 className="font-semibold text-slate-900">
              직급 관리
            </h2>

            <p className="mt-1 text-xs text-slate-500">
              높은 직급부터 낮은 직급 순서로 표시됩니다.
            </p>
          </div>

          <div className="flex items-center gap-2">
            <button
              type="button"
              onClick={handleOpenUpdateModal}
              disabled={!selectedPosition || isLoading}
              className="rounded border px-3 py-1.5 text-sm text-slate-700 disabled:cursor-not-allowed disabled:opacity-50"
            >
              수정
            </button>

            <button
              type="button"
              onClick={handleOpenCreateModal}
              className="rounded bg-blue-600 px-3 py-1.5 text-sm font-medium text-white hover:bg-blue-700"
            >
              직급 추가
            </button>
          </div>
        </div>

        {notice.message && (
          <div
            className={`mt-4 whitespace-pre-line rounded px-3 py-2 text-sm ${
              notice.type === "success"
                ? "bg-green-50 text-green-700"
                : "bg-red-50 text-red-700"
            }`}
          >
            {notice.message}
          </div>
        )}

        <div className="mt-4 overflow-hidden rounded border">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="px-3 py-2 font-medium">
                  직급명
                </th>
                <th className="px-3 py-2 font-medium">
                  직급 코드
                </th>
                <th className="px-3 py-2 font-medium">
                  사용 여부
                </th>
              </tr>
            </thead>

            <tbody>
              {isLoading && (
                <tr>
                  <td
                    colSpan={3}
                    className="px-3 py-6 text-center text-slate-500"
                  >
                    직급 목록을 불러오는 중입니다.
                  </td>
                </tr>
              )}

              {!isLoading && sortedPositions.length === 0 && (
                <tr>
                  <td
                    colSpan={3}
                    className="px-3 py-6 text-center text-slate-500"
                  >
                    등록된 직급이 없습니다.
                  </td>
                </tr>
              )}

              {!isLoading &&
                sortedPositions.map((position) => {
                  const isSelected =
                    position.positionId === selectedPositionId;

                  return (
                    <tr
                      key={position.positionId}
                      onClick={() =>
                        handleSelectPosition(position.positionId)
                      }
                      className={`cursor-pointer border-t ${
                        isSelected
                          ? "bg-blue-50"
                          : "hover:bg-slate-50"
                      }`}
                    >
                      <td className="px-3 py-2">
                        <div className="font-medium text-slate-900">
                          {position.positionName}
                        </div>
                      </td>

                      <td className="px-3 py-2 text-slate-600">
                        {position.positionCode}
                      </td>

                      <td className="px-3 py-2">
                        <span
                          className={`rounded-full px-2 py-0.5 text-xs ${
                            position.isActive === "Y"
                              ? "bg-green-50 text-green-700"
                              : "bg-slate-100 text-slate-500"
                          }`}
                        >
                          {position.isActive === "Y" ? "사용" : "미사용"}
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
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30 px-4">
          <div className="w-full max-w-md rounded-lg bg-white p-5 shadow-lg">
            <div className="mb-4">
              <h3 className="text-lg font-semibold text-slate-900">
                {modalMode === "create" ? "직급 추가" : "직급 수정"}
              </h3>
            </div>

            {modalError && (
              <div className="mb-4 whitespace-pre-line rounded bg-red-50 px-3 py-2 text-sm text-red-700">
                {modalError}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              <FormField label="직급 코드">
                <input
                  type="text"
                  name="positionCode"
                  value={form.positionCode}
                  onChange={handleChange}
                  disabled={modalMode === "update"}
                  placeholder="예: MANAGER"
                  className="w-full rounded border px-3 py-2 text-sm disabled:bg-slate-100 disabled:text-slate-500"
                />
              </FormField>

              <FormField label="직급명">
                <input
                  type="text"
                  name="positionName"
                  value={form.positionName}
                  onChange={handleChange}
                  placeholder="예: 과장"
                  className="w-full rounded border px-3 py-2 text-sm"
                />
              </FormField>

              {modalMode === "update" && (
                <FormField label="사용 여부">
                  <select
                    name="isActive"
                    value={form.isActive}
                    onChange={handleChange}
                    className="w-full rounded border px-3 py-2 text-sm"
                  >
                    <option value="Y">사용</option>
                    <option value="N">미사용</option>
                  </select>
                </FormField>
              )}

              <FormField label="바로 위 직급">
                <select
                  name="upperPositionId"
                  value={form.upperPositionId}
                  onChange={handleChange}
                  disabled={modalMode === "update" && form.isActive === "N"}
                  className="w-full rounded border px-3 py-2 text-sm disabled:bg-slate-100 disabled:text-slate-500"
                >
                  <option value="">없음 - 최상위 직급</option>

                  {activePositionOptions.map((position) => (
                    <option
                      key={position.positionId}
                      value={position.positionId}
                    >
                      {position.positionName}
                    </option>
                  ))}
                </select>

                <p className="mt-1 text-xs text-slate-500">
                  선택한 직급보다 한 단계 낮은 위치에 저장됩니다.
                </p>
              </FormField>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={handleCloseModal}
                  disabled={isSaving}
                  className="rounded border px-4 py-2 text-sm text-slate-700 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  취소
                </button>

                <button
                  type="submit"
                  disabled={isSaving}
                  className="rounded bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {isSaving ? "저장 중..." : "저장"}
                </button>
              </div>
            </form>
          </div>
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