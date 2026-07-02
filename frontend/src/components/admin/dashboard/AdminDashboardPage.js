import EmptyDashboardSlot from "./EmptyDashboardSlot";
import HrEmployeeFlowCard from "./HrEmployeeFlowCard";
import MonthlyCost from "../../payroll/dashboard/MonthlyCost";
import DepartmentCost from "../../payroll/dashboard/DepartmentCost";
import MonthlyAttendanceCard from "./MonthlyAttendanceCard";

export default function AdminDashboardPage() {
  return (
    <div className="flex h-full min-h-0 flex-col p-4">
      <div className="grid min-h-0 flex-1 gap-4">
        <div className="grid min-h-0 grid-cols-1 gap-4">
          <MonthlyCost />
        </div>

        <div className="grid min-h-0 grid-cols-2 gap-4 md:grid-cols-2">
          <div className="flex-1 p-6">
            <HrEmployeeFlowCard />
          </div>
          <DepartmentCost />
        </div>
      </div>
    </div>
  );
}
