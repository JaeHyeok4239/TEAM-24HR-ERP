"use client";

import { useEffect, useState } from "react";

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
import EmployeeListPanel from "@/components/hr/employees/EmployeeListPanel";

import { Button } from "@/components/ui/button";

const EMPLOYMENT_TYPE = {
  REGULAR: "REGULAR",
  DAILY: "DAILY",
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

  const reloadCurrentEmployeeList = () => {
    fetchEmployees({
      departmentId: selectedDepartmentId,
      employmentType: selectedEmploymentType,
      keywordValue: keyword.trim(),
    });
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

  const handleEmployeeUpdated = (updatedEmployee) => {
    setSelectedEmployee(updatedEmployee);

    setEmployees((prevEmployees) =>
      prevEmployees.map((employee) =>
        employee.employeeId === updatedEmployee.employeeId
          ? {
              ...employee,
              name: updatedEmployee.name,
              phone: updatedEmployee.phone,
              email: updatedEmployee.email,
              departmentName: updatedEmployee.departmentName,
              positionName: updatedEmployee.positionName,
              employmentType: updatedEmployee.employmentType,
              status: updatedEmployee.status,
            }
          : employee,
      ),
    );

    fetchDepartmentTree();
    reloadCurrentEmployeeList();
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

          <EmployeeListPanel
            employees={employees}
            selectedEmployeeId={selectedEmployee?.employeeId}
            keyword={keyword}
            isLoading={isEmployeesLoading}
            onKeywordChange={setKeyword}
            onKeywordKeyDown={handleKeywordKeyDown}
            onSearch={handleSearch}
            onSelectEmployee={handleSelectEmployee}
          />

          <EmployeeDetailPanel
            employee={selectedEmployee}
            isLoading={isEmployeeDetailLoading}
            onEmployeeUpdated={handleEmployeeUpdated}
            options={{
              departments: departmentOptions,
              positions: positionOptions,
            }}
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