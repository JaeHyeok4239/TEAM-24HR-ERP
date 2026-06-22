"use client";

import { useEffect, useState } from "react";
import { Search } from "lucide-react";

import Header from "@/components/Header";

import {
  getHrDepartmentTreeRequest,
  getHrEmployeeDetailRequest,
  getHrEmployeeFormOptionsRequest,
  getHrEmployeesRequest,
} from "@/services/hrEmployeeService";

import EmployeeCreateDialog from "@/components/hr/employees/EmployeeCreateDialog";
import EmployeeDepartmentTree from "@/components/hr/employees/EmployeeDepartmentTree";
import EmployeeDetailPanel from "@/components/hr/employees/EmployeeDetailPanel";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";

const EMPLOYMENT_TYPE = {
  REGULAR: "REGULAR",
  DAILY: "DAILY",
};

const STATUS_LABELS = {
  ACTIVE: "재직",
  LEAVE: "휴직",
  RESIGNED: "퇴사",
  RETIRED: "퇴사",
  LOCKED: "잠김",
};

export default function HrEmployeesPage() {
  const [departmentTree, setDepartmentTree] = useState(null);
  const [employees, setEmployees] = useState([]);
  const [selectedEmployee, setSelectedEmployee] = useState(null);

  const [departmentOptions, setDepartmentOptions] = useState([]);
  const [positionOptions, setPositionOptions] = useState([]);

  const [selectedDepartmentId, setSelectedDepartmentId] = useState("");
  const [selectedEmploymentType, setSelectedEmploymentType] = useState(
    EMPLOYMENT_TYPE.REGULAR,
  );

  const [keyword, setKeyword] = useState("");

  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [isDepartmentTreeLoading, setIsDepartmentTreeLoading] = useState(true);
  const [isEmployeesLoading, setIsEmployeesLoading] = useState(true);
  const [isEmployeeDetailLoading, setIsEmployeeDetailLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    let ignore = false;

    const loadInitialData = async () => {
      try {
        const [departmentTreeData, employeeData, formOptionsData] =
          await Promise.all([
            getHrDepartmentTreeRequest(),
            getHrEmployeesRequest({
              employmentType: EMPLOYMENT_TYPE.REGULAR,
            }),
            getHrEmployeeFormOptionsRequest(),
          ]);

        if (ignore) {
          return;
        }

        setDepartmentTree(departmentTreeData);
        setEmployees(employeeData);
        setDepartmentOptions(formOptionsData.departments ?? []);
        setPositionOptions(formOptionsData.positions ?? []);
      } catch (error) {
        if (!ignore) {
          setErrorMessage(
            error.message || "직원관리 정보를 불러오지 못했습니다.",
          );
        }
      } finally {
        if (!ignore) {
          setIsDepartmentTreeLoading(false);
          setIsEmployeesLoading(false);
        }
      }
    };

    loadInitialData();

    return () => {
      ignore = true;
    };
  }, []);

  const fetchDepartmentTree = async () => {
    try {
      setIsDepartmentTreeLoading(true);

      const data = await getHrDepartmentTreeRequest();

      setDepartmentTree(data);
    } catch (error) {
      setErrorMessage(error.message || "부서 정보를 불러오지 못했습니다.");
    } finally {
      setIsDepartmentTreeLoading(false);
    }
  };

  const fetchEmployees = async ({
    departmentId,
    employmentType,
    keywordValue,
  }) => {
    try {
      setIsEmployeesLoading(true);
      setErrorMessage("");

      const data = await getHrEmployeesRequest({
        departmentId,
        employmentType,
        keyword: keywordValue,
      });

      setEmployees(data);
    } catch (error) {
      setErrorMessage(error.message || "직원 목록을 불러오지 못했습니다.");
    } finally {
      setIsEmployeesLoading(false);
    }
  };

  const fetchEmployeeDetail = async (employeeId) => {
    try {
      setIsEmployeeDetailLoading(true);
      setErrorMessage("");

      const data = await getHrEmployeeDetailRequest(employeeId);

      setSelectedEmployee(data);
    } catch (error) {
      setErrorMessage(error.message || "직원 상세 정보를 불러오지 못했습니다.");
    } finally {
      setIsEmployeeDetailLoading(false);
    }
  };

  const handleSearch = () => {
    setSelectedEmployee(null);

    fetchEmployees({
      departmentId: selectedDepartmentId,
      employmentType: selectedEmploymentType,
      keywordValue: keyword.trim(),
    });
  };

  const handleKeywordKeyDown = (event) => {
    if (event.key === "Enter") {
      handleSearch();
    }
  };

  const handleSelectAllEmployees = () => {
    setSelectedDepartmentId("");
    setSelectedEmploymentType(EMPLOYMENT_TYPE.REGULAR);
    setSelectedEmployee(null);

    fetchEmployees({
      departmentId: "",
      employmentType: EMPLOYMENT_TYPE.REGULAR,
      keywordValue: keyword.trim(),
    });
  };

  const handleSelectDepartment = (departmentId) => {
    const nextDepartmentId = String(departmentId);

    setSelectedDepartmentId(nextDepartmentId);
    setSelectedEmploymentType(EMPLOYMENT_TYPE.REGULAR);
    setSelectedEmployee(null);

    fetchEmployees({
      departmentId: nextDepartmentId,
      employmentType: EMPLOYMENT_TYPE.REGULAR,
      keywordValue: keyword.trim(),
    });
  };

  const handleSelectDailyEmployees = () => {
    setSelectedDepartmentId("");
    setSelectedEmploymentType(EMPLOYMENT_TYPE.DAILY);
    setSelectedEmployee(null);

    fetchEmployees({
      departmentId: "",
      employmentType: EMPLOYMENT_TYPE.DAILY,
      keywordValue: keyword.trim(),
    });
  };

  const handleSelectEmployee = (employeeId) => {
    fetchEmployeeDetail(employeeId);
  };

  const handleEmployeeCreated = (createdEmployee) => {
    const nextEmploymentType =
      createdEmployee.employmentType ?? EMPLOYMENT_TYPE.REGULAR;

    const nextDepartmentId =
      nextEmploymentType === EMPLOYMENT_TYPE.REGULAR
        ? String(createdEmployee.departmentId ?? "")
        : "";

    setKeyword("");
    setSelectedEmployee(createdEmployee);
    setSelectedEmploymentType(nextEmploymentType);
    setSelectedDepartmentId(nextDepartmentId);

    fetchDepartmentTree();

    fetchEmployees({
      departmentId: nextDepartmentId,
      employmentType: nextEmploymentType,
      keywordValue: "",
    });
  };

  return (
    <>
      <Header title="직원 목록" />

      <main className="p-6">
        <div className="mb-4 flex items-center justify-end">
          <Button
            type="button"
            size="sm"
            onClick={() => setIsCreateDialogOpen(true)}
          >
            + 새 직원
          </Button>
        </div>

        {errorMessage && (
          <div className="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {errorMessage}
          </div>
        )}

        <div className="grid h-[calc(100vh-168px)] min-h-[680px] grid-cols-[280px_360px_minmax(0,1fr)] gap-4">
          <EmployeeDepartmentTree
            departmentTree={departmentTree}
            selectedDepartmentId={selectedDepartmentId}
            selectedEmploymentType={selectedEmploymentType}
            isLoading={isDepartmentTreeLoading}
            onSelectAllEmployees={handleSelectAllEmployees}
            onSelectDepartment={handleSelectDepartment}
            onSelectDailyEmployees={handleSelectDailyEmployees}
          />

          <Card className="flex h-full flex-col overflow-hidden rounded-xl border-slate-200 bg-white shadow-sm">
            <CardHeader className="flex h-[56px] shrink-0 justify-center border-b px-4 py-0">
              <CardTitle className="text-left text-base font-semibold text-slate-900">
                직원 목록
              </CardTitle>
            </CardHeader>

            <div className="shrink-0 border-b bg-white px-3 py-3">
              <div className="flex gap-2">
                <div className="relative flex-1">
                  <Search
                    size={15}
                    className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
                  />

                  <Input
                    value={keyword}
                    onChange={(event) => setKeyword(event.target.value)}
                    onKeyDown={handleKeywordKeyDown}
                    placeholder="이름, 사번, 로그인 ID 검색"
                    className="h-9 pl-9 text-sm"
                  />
                </div>

                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={handleSearch}
                >
                  검색
                </Button>
              </div>
            </div>

            <CardContent className="min-h-0 flex-1 p-0">
              {isEmployeesLoading ? (
                <div className="flex h-full items-center justify-center text-sm text-slate-400">
                  직원 목록을 불러오는 중...
                </div>
              ) : employees.length === 0 ? (
                <div className="flex h-full items-center justify-center text-sm text-slate-400">
                  조회된 직원이 없습니다.
                </div>
              ) : (
                <div className="h-full overflow-y-auto">
                  {employees.map((employee) => {
                    const selected =
                      selectedEmployee?.employeeId === employee.employeeId;

                    return (
                      <button
                        key={employee.employeeId}
                        type="button"
                        onClick={() => handleSelectEmployee(employee.employeeId)}
                        className={`flex w-full items-start gap-3 border-b px-4 py-3 text-left transition hover:bg-slate-50 ${
                          selected ? "bg-slate-100" : "bg-white"
                        }`}
                      >
                        <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-slate-300 text-xs font-semibold text-white">
                          {employee.name?.slice(-2)}
                        </div>

                        <div className="min-w-0 flex-1">
                          <div className="flex items-center gap-2">
                            <p className="truncate text-sm font-semibold text-slate-900">
                              {employee.name}
                            </p>

                            <Badge variant="outline">
                              {STATUS_LABELS[employee.status] ??
                                employee.status}
                            </Badge>
                          </div>

                          <p className="mt-1 truncate text-xs text-slate-500">
                            {employee.positionName ?? "-"} ·{" "}
                            {employee.departmentName ?? "-"}
                          </p>

                          <p className="mt-1 truncate text-xs text-slate-400">
                            {employee.email ?? "-"}
                          </p>
                        </div>
                      </button>
                    );
                  })}
                </div>
              )}
            </CardContent>
          </Card>

          <EmployeeDetailPanel
            employee={selectedEmployee}
            isLoading={isEmployeeDetailLoading}
          />
        </div>

        <EmployeeCreateDialog
          open={isCreateDialogOpen}
          onOpenChange={setIsCreateDialogOpen}
          departmentOptions={departmentOptions}
          positionOptions={positionOptions}
          onCreated={handleEmployeeCreated}
        />
      </main>
    </>
  );
}