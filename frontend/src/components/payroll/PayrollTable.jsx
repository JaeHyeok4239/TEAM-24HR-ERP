"use client";

export default function PayrollTable({
  payrolls,
  onRowClick,
}) {
  return (
    <table className="w-full border-collapse">
      <thead>
        <tr className="bg-slate-100">
          <th className="border p-2">사번</th>
          <th className="border p-2">이름</th>
          <th className="border p-2">부서</th>
          <th className="border p-2">급여월</th>
          <th className="border p-2">총지급액</th>
          <th className="border p-2">공제액</th>
          <th className="border p-2">실수령액</th>
          <th className="border p-2">상태</th>
        </tr>
      </thead>

      <tbody>
        {payrolls.map((item) => (
          <tr
            key={item.payrollId}
            className="cursor-pointer hover:bg-slate-50"
            onClick={() =>
              onRowClick(item.payrollId)
            }
          >
            <td className="border p-2">
              {item.employeeNo}
            </td>

            <td className="border p-2">
              {item.employeeName}
            </td>

            <td className="border p-2">
              {item.departmentName}
            </td>

            <td className="border p-2">
              {item.payMonth}
            </td>

            <td className="border p-2 text-right">
              {item.totalPay.toLocaleString()}
            </td>

            <td className="border p-2 text-right">
              {item.totalDeduction.toLocaleString()}
            </td>

            <td className="border p-2 text-right font-bold">
              {item.netSalary.toLocaleString()}
            </td>

            <td className="border p-2">
              {item.status}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}