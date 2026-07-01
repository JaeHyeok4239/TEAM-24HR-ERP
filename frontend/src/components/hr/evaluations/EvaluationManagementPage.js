"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import {
  Award,
  CalendarDays,
  ClipboardCheck,
  FileText,
  Plus,
  RotateCcw,
  Users,
} from "lucide-react";

import {
  closeEvaluationPeriodRequest,
  confirmEmployeeEvaluationRequest,
  createEvaluationPeriodRequest,
  createEvaluationTargetsRequest,
  getEmployeeEvaluationDetailRequest,
  getEmployeeEvaluationHistoriesRequest,
  getEvaluationPeriodsRequest,
  getEvaluationResultsRequest,
  getEvaluationTargetsRequest,
  getPromotionCandidatesRequest,
  openEvaluationPeriodRequest,
  saveEmployeeEvaluationRequest,
} from "@/services/evaluationService";

import {
  HALF_TYPE_LABELS,
  INITIAL_PERIOD_FORM,
  TABS,
} from "./evaluationConstants";

import {
  Card,
  CardTitle,
  EmptyBox,
  StatusBadge,
  formatDate,
} from "./EvaluationCommon";

import EmployeeHistoryPanel from "./EmployeeHistoryPanel";
import EvaluationModal from "./EvaluationModal";
import PeriodCreateForm from "./PeriodCreateForm";
import PromotionCandidateTable from "./PromotionCandidateTable";
import ResultTable from "./ResultTable";
import SelectedPeriodActionCard from "./SelectedPeriodActionCard";
import TargetTable from "./TargetTable";

export default function EvaluationManagementPage() {
  const [activeTab, setActiveTab] = useState("manage");

  const [periods, setPeriods] = useState([]);
  const [selectedPeriodId, setSelectedPeriodId] = useState("");

  const [targets, setTargets] = useState([]);
  const [results, setResults] = useState([]);
  const [promotionCandidates, setPromotionCandidates] = useState([]);

  const [selectedResultEmployee, setSelectedResultEmployee] = useState(null);
  const [employeeHistories, setEmployeeHistories] = useState([]);

  const [periodForm, setPeriodForm] = useState(INITIAL_PERIOD_FORM);
  const [isPeriodFormOpen, setIsPeriodFormOpen] = useState(false);

  const [evaluationDetail, setEvaluationDetail] = useState(null);
  const [evaluationForm, setEvaluationForm] = useState({
    answers: [],
    comment: "",
  });

  const [isLoadingPeriods, setIsLoadingPeriods] = useState(false);
  const [isLoadingTargets, setIsLoadingTargets] = useState(false);
  const [isLoadingResults, setIsLoadingResults] = useState(false);
  const [isLoadingPromotion, setIsLoadingPromotion] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  const selectedPeriod = useMemo(
    () =>
      periods.find(
        (period) =>
          String(period.evaluationPeriodId) === String(selectedPeriodId),
      ) ?? null,
    [periods, selectedPeriodId],
  );

  const totalScore = useMemo(
    () =>
      evaluationForm.answers.reduce((sum, answer) => {
        const score = Number(answer.score);

        if (Number.isNaN(score)) {
          return sum;
        }

        return sum + score;
      }, 0),
    [evaluationForm.answers],
  );

  const isSelectedPeriodClosed = selectedPeriod?.status === "CLOSED";
  const isSelectedPeriodOpen = selectedPeriod?.status === "OPEN";

  const loadPeriods = useCallback(async () => {
    try {
      setIsLoadingPeriods(true);

      const data = await getEvaluationPeriodsRequest();

      setPeriods(data);

      if (!selectedPeriodId && data.length > 0) {
        setSelectedPeriodId(String(data[0].evaluationPeriodId));
      }
    } catch (error) {
      // (추후/나중에 콘솔에러 삭제 예정)
      console.error(error);
      alert(error.message || "평가기간 조회 중 오류가 발생했습니다.");
    } finally {
      setIsLoadingPeriods(false);
    }
  }, [selectedPeriodId]);

  const loadTargets = useCallback(async (periodId) => {
    if (!periodId) {
      setTargets([]);
      return;
    }

    try {
      setIsLoadingTargets(true);

      const data = await getEvaluationTargetsRequest(periodId);

      setTargets(data);
    } catch (error) {
      console.error(error);
      alert(error.message || "평가 대상자 조회 중 오류가 발생했습니다.");
    } finally {
      setIsLoadingTargets(false);
    }
  }, []);

  const loadResults = useCallback(async () => {
    try {
      setIsLoadingResults(true);

      const data = await getEvaluationResultsRequest();

      setResults(data);
    } catch (error) {
      console.error(error);
      alert(error.message || "평가 결과 조회 중 오류가 발생했습니다.");
    } finally {
      setIsLoadingResults(false);
    }
  }, []);

  const loadPromotionCandidates = useCallback(async () => {
    try {
      setIsLoadingPromotion(true);

      const data = await getPromotionCandidatesRequest();

      setPromotionCandidates(data);
    } catch (error) {
      console.error(error);
      alert(error.message || "진급심사 대상자 조회 중 오류가 발생했습니다.");
    } finally {
      setIsLoadingPromotion(false);
    }
  }, []);

  useEffect(() => {
    let ignore = false;

    const loadInitialData = async () => {
      await Promise.all([
        loadPeriods(),
        loadResults(),
        loadPromotionCandidates(),
      ]);
    };

    if (!ignore) {
      loadInitialData();
    }

    return () => {
      ignore = true;
    };
  }, [loadPeriods, loadResults, loadPromotionCandidates]);

  useEffect(() => {
    if (!selectedPeriodId) {
      return;
    }

    let ignore = false;

    const loadPeriodTargets = async () => {
      await loadTargets(selectedPeriodId);
    };

    if (!ignore) {
      loadPeriodTargets();
    }

    return () => {
      ignore = true;
    };
  }, [selectedPeriodId, loadTargets]);

  const handlePeriodFormChange = (event) => {
    const { name, value } = event.target;

    setPeriodForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleCreatePeriod = async () => {
    if (!periodForm.evaluationYear) {
      alert("평가연도를 입력해주세요.");
      return;
    }

    if (!periodForm.halfType) {
      alert("반기 구분을 선택해주세요.");
      return;
    }

    if (
      !periodForm.targetStartDate ||
      !periodForm.targetEndDate ||
      !periodForm.inputStartDate ||
      !periodForm.inputEndDate
    ) {
      alert("평가 대상 기간과 입력 기간을 모두 입력해주세요.");
      return;
    }

    try {
      setIsSaving(true);

      const createdPeriod = await createEvaluationPeriodRequest(periodForm);

      setPeriodForm(INITIAL_PERIOD_FORM);
      setIsPeriodFormOpen(false);
      setSelectedPeriodId(String(createdPeriod.evaluationPeriodId));

      await loadPeriods();

      alert("평가기간이 생성되었습니다.");
    } catch (error) {
      console.error(error);
      alert(error.message || "평가기간 생성 중 오류가 발생했습니다.");
    } finally {
      setIsSaving(false);
    }
  };

  const handleOpenPeriod = async () => {
    if (!selectedPeriod?.evaluationPeriodId) {
      alert("평가기간을 선택해주세요.");
      return;
    }

    try {
      setIsSaving(true);

      await openEvaluationPeriodRequest(selectedPeriod.evaluationPeriodId);
      await loadPeriods();

      alert("평가가 시작되었습니다.");
    } catch (error) {
      console.error(error);
      alert(error.message || "평가 시작 중 오류가 발생했습니다.");
    } finally {
      setIsSaving(false);
    }
  };

  const handleClosePeriod = async () => {
    if (!selectedPeriod?.evaluationPeriodId) {
      alert("평가기간을 선택해주세요.");
      return;
    }

    const confirmed = window.confirm(
      "평가를 마감하면 더 이상 평가를 수정할 수 없습니다. 마감하시겠습니까?",
    );

    if (!confirmed) {
      return;
    }

    try {
      setIsSaving(true);

      await closeEvaluationPeriodRequest(selectedPeriod.evaluationPeriodId);
      await loadPeriods();
      await loadResults();
      await loadPromotionCandidates();

      alert("평가가 마감되었습니다.");
    } catch (error) {
      console.error(error);
      alert(error.message || "평가 마감 중 오류가 발생했습니다.");
    } finally {
      setIsSaving(false);
    }
  };

  const handleCreateTargets = async () => {
    if (!selectedPeriod?.evaluationPeriodId) {
      alert("평가기간을 선택해주세요.");
      return;
    }

    try {
      setIsSaving(true);

      const data = await createEvaluationTargetsRequest(
        selectedPeriod.evaluationPeriodId,
      );

      setTargets(data);

      alert("평가 대상자가 생성되었습니다.");
    } catch (error) {
      console.error(error);
      alert(error.message || "평가 대상자 생성 중 오류가 발생했습니다.");
    } finally {
      setIsSaving(false);
    }
  };

  const handleOpenEvaluationModal = async (employeeEvaluationId) => {
    try {
      setIsSaving(true);

      const detail =
        await getEmployeeEvaluationDetailRequest(employeeEvaluationId);

      setEvaluationDetail(detail);
      setEvaluationForm({
        answers: (detail.answers ?? []).map((answer) => ({
          evaluationQuestionId: answer.evaluationQuestionId,
          score: answer.score ?? "",
        })),
        comment: detail.comment ?? "",
      });
    } catch (error) {
      console.error(error);
      alert(error.message || "평가 상세 조회 중 오류가 발생했습니다.");
    } finally {
      setIsSaving(false);
    }
  };

  const handleCloseEvaluationModal = () => {
    setEvaluationDetail(null);
    setEvaluationForm({
      answers: [],
      comment: "",
    });
  };

  const handleAnswerChange = (evaluationQuestionId, score) => {
    setEvaluationForm((prev) => ({
      ...prev,
      answers: prev.answers.map((answer) =>
        answer.evaluationQuestionId === evaluationQuestionId
          ? {
              ...answer,
              score,
            }
          : answer,
      ),
    }));
  };

  const handleCommentChange = (event) => {
    const { value } = event.target;

    setEvaluationForm((prev) => ({
      ...prev,
      comment: value,
    }));
  };

  const validateEvaluationAnswers = () => {
    if (!evaluationForm.answers.length) {
      alert("평가 문항이 없습니다.");
      return false;
    }

    const invalidAnswer = evaluationForm.answers.find(
      (answer) =>
        !answer.score || Number(answer.score) < 1 || Number(answer.score) > 5,
    );

    if (invalidAnswer) {
      alert("모든 문항의 점수를 1점부터 5점까지 선택해주세요.");
      return false;
    }

    return true;
  };

  const handleSaveEvaluation = async () => {
    if (!evaluationDetail?.employeeEvaluationId) {
      return;
    }

    if (!validateEvaluationAnswers()) {
      return;
    }

    try {
      setIsSaving(true);

      const payload = {
        answers: evaluationForm.answers.map((answer) => ({
          evaluationQuestionId: answer.evaluationQuestionId,
          score: Number(answer.score),
        })),
        comment: evaluationForm.comment,
      };

      const updatedDetail = await saveEmployeeEvaluationRequest(
        evaluationDetail.employeeEvaluationId,
        payload,
      );

      setEvaluationDetail(updatedDetail);
      await loadTargets(selectedPeriodId);

      alert("평가가 임시저장되었습니다.");
    } catch (error) {
      console.error(error);
      alert(error.message || "평가 저장 중 오류가 발생했습니다.");
    } finally {
      setIsSaving(false);
    }
  };

  const handleConfirmEvaluation = async () => {
    if (!evaluationDetail?.employeeEvaluationId) {
      return;
    }

    if (!validateEvaluationAnswers()) {
      return;
    }

    const confirmed = window.confirm(
      "평가를 확정하면 수정할 수 없습니다. 확정하시겠습니까?",
    );

    if (!confirmed) {
      return;
    }

    try {
      setIsSaving(true);

      const payload = {
        answers: evaluationForm.answers.map((answer) => ({
          evaluationQuestionId: answer.evaluationQuestionId,
          score: Number(answer.score),
        })),
        comment: evaluationForm.comment,
      };

      const updatedDetail = await confirmEmployeeEvaluationRequest(
        evaluationDetail.employeeEvaluationId,
        payload,
      );

      setEvaluationDetail(updatedDetail);

      await loadTargets(selectedPeriodId);
      await loadResults();
      await loadPromotionCandidates();

      alert("평가가 확정되었습니다.");
    } catch (error) {
      console.error(error);
      alert(error.message || "평가 확정 중 오류가 발생했습니다.");
    } finally {
      setIsSaving(false);
    }
  };

  const handleSelectResultEmployee = async (employee) => {
    try {
      setSelectedResultEmployee(employee);
      setEmployeeHistories([]);

      const histories = await getEmployeeEvaluationHistoriesRequest(
        employee.employeeId,
      );

      setEmployeeHistories(histories);
    } catch (error) {
      console.error(error);
      alert(error.message || "직원 평가 이력 조회 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 px-8 py-8">
      <div className="mx-auto max-w-[1700px] space-y-6">
        <div className="flex gap-2 rounded-xl border border-slate-200 bg-white p-1 shadow-sm">
          {TABS.map((tab) => {
            const selected = activeTab === tab.value;

            return (
              <button
                key={tab.value}
                type="button"
                onClick={() => setActiveTab(tab.value)}
                className={`rounded-lg px-4 py-2 text-sm font-semibold transition ${
                  selected
                    ? "bg-slate-900 text-white"
                    : "text-slate-500 hover:bg-slate-100 hover:text-slate-900"
                }`}
              >
                {tab.label}
              </button>
            );
          })}
        </div>

        {activeTab === "manage" && (
          <div className="grid grid-cols-[360px_1fr] gap-6">
            <section className="space-y-4">
              <SelectedPeriodActionCard
                selectedPeriod={selectedPeriod}
                targetCount={targets.length}
                isSaving={isSaving}
                onCreateTargets={handleCreateTargets}
                onOpen={handleOpenPeriod}
                onClose={handleClosePeriod}
              />

              <Card>
                <CardTitle
                  icon={<CalendarDays size={17} />}
                  title="평가기간"
                  action={
                    <button
                      type="button"
                      onClick={() => setIsPeriodFormOpen((prev) => !prev)}
                      className="inline-flex items-center gap-1 rounded-md bg-slate-900 px-3 py-1.5 text-xs font-semibold text-white"
                    >
                      <Plus size={14} />
                      생성
                    </button>
                  }
                />

                {isPeriodFormOpen && (
                  <PeriodCreateForm
                    form={periodForm}
                    isSaving={isSaving}
                    onChange={handlePeriodFormChange}
                    onSubmit={handleCreatePeriod}
                    onCancel={() => setIsPeriodFormOpen(false)}
                  />
                )}

                <div className="mt-4 space-y-2">
                  {isLoadingPeriods ? (
                    <EmptyBox message="평가기간을 불러오는 중입니다." />
                  ) : periods.length === 0 ? (
                    <EmptyBox message="등록된 평가기간이 없습니다." />
                  ) : (
                    periods.map((period) => (
                      <button
                        key={period.evaluationPeriodId}
                        type="button"
                        onClick={() =>
                          setSelectedPeriodId(String(period.evaluationPeriodId))
                        }
                        className={`w-full rounded-lg border px-4 py-3 text-left transition ${
                          String(selectedPeriodId) ===
                          String(period.evaluationPeriodId)
                            ? "border-slate-900 bg-slate-900 text-white"
                            : "border-slate-200 bg-white text-slate-700 hover:bg-slate-50"
                        }`}
                      >
                        <div className="flex items-center justify-between gap-2">
                          <p className="truncate text-sm font-bold">
                            {period.periodName}
                          </p>

                          <StatusBadge status={period.status} />
                        </div>

                        <p className="mt-2 text-xs opacity-75">
                          {period.evaluationYear}년{" "}
                          {HALF_TYPE_LABELS[period.halfType] ?? period.halfType}
                        </p>

                        <p className="mt-1 text-xs opacity-75">
                          입력기간: {formatDate(period.inputStartDate)} ~{" "}
                          {formatDate(period.inputEndDate)}
                        </p>
                      </button>
                    ))
                  )}
                </div>
              </Card>
            </section>

            <section>
              <Card>
                <CardTitle
                  icon={<Users size={17} />}
                  title="평가 대상자"
                  description="대상자 클릭 시 평가지를 입력할 수 있습니다."
                  action={
                    <button
                      type="button"
                      onClick={() => loadTargets(selectedPeriodId)}
                      className="inline-flex items-center gap-1 rounded-md border border-slate-300 bg-white px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-50"
                    >
                      <RotateCcw size={14} />
                      새로고침
                    </button>
                  }
                />

                <TargetTable
                  targets={targets}
                  isLoading={isLoadingTargets}
                  selectedPeriod={selectedPeriod}
                  onOpenEvaluation={handleOpenEvaluationModal}
                />
              </Card>
            </section>
          </div>
        )}

        {activeTab === "results" && (
          <div className="grid grid-cols-[1fr_420px] gap-6">
            <Card>
              <CardTitle
                icon={<FileText size={17} />}
                title="평가 결과 조회"
                description="확정된 평가만 누적점수에 반영됩니다."
                action={
                  <button
                    type="button"
                    onClick={loadResults}
                    className="inline-flex items-center gap-1 rounded-md border border-slate-300 bg-white px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-50"
                  >
                    <RotateCcw size={14} />
                    새로고침
                  </button>
                }
              />

              <ResultTable
                results={results}
                isLoading={isLoadingResults}
                selectedEmployeeId={selectedResultEmployee?.employeeId}
                onSelectEmployee={handleSelectResultEmployee}
              />
            </Card>

            <Card>
              <CardTitle
                icon={<ClipboardCheck size={17} />}
                title="직원 평가 이력"
                description="직원을 선택하면 확정 평가 이력이 표시됩니다."
              />

              <EmployeeHistoryPanel
                selectedEmployee={selectedResultEmployee}
                histories={employeeHistories}
              />
            </Card>
          </div>
        )}

        {activeTab === "promotion" && (
          <Card>
            <CardTitle
              icon={<Award size={17} />}
              title="진급심사 대상자"
              description="승진 기준을 충족한 직원을 조회합니다."
              action={
                <button
                  type="button"
                  onClick={loadPromotionCandidates}
                  className="inline-flex items-center gap-1 rounded-md border border-slate-300 bg-white px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-50"
                >
                  <RotateCcw size={14} />
                  새로고침
                </button>
              }
            />

            <PromotionCandidateTable
              candidates={promotionCandidates}
              isLoading={isLoadingPromotion}
            />
          </Card>
        )}
      </div>

      {evaluationDetail && (
        <EvaluationModal
          detail={evaluationDetail}
          form={evaluationForm}
          totalScore={totalScore}
          isSaving={isSaving}
          isPeriodClosed={isSelectedPeriodClosed}
          isPeriodOpen={isSelectedPeriodOpen}
          onClose={handleCloseEvaluationModal}
          onAnswerChange={handleAnswerChange}
          onCommentChange={handleCommentChange}
          onSave={handleSaveEvaluation}
          onConfirm={handleConfirmEvaluation}
        />
      )}
    </div>
  );
}
