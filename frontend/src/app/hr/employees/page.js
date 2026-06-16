import Header from "@/components/Header";
import EmployeeList from "@/components/hr/EmployeeList";

export default function EmployeesPage() {
  return (
    <>
      <Header title="직원 목록" />
      <EmployeeList />
    </>
  );
}