"use client";

import { useEffect, useState } from "react";
import { getPayrolls } from "@/services/payrollService";

export default function PayrollPage() {

    const [payrolls, setPayrolls] = useState([]);

    useEffect(() => {

        const loadData = async () => {

            const data = await getPayrolls();

            setPayrolls(data);
        };

        loadData();

    }, []);

    return (
        <div>
            <h1>급여대장</h1>

            <table>

                <thead>
                    <tr>
                        <th>사번</th>
                        <th>이름</th>
                        <th>부서</th>
                        <th>실수령액</th>
                    </tr>
                </thead>

                <tbody>

                    {payrolls.map(payroll => (

                        <tr key={payroll.payrollId}>
                            <td>{payroll.employeeNo}</td>
                            <td>{payroll.employeeName}</td>
                            <td>{payroll.departmentName}</td>
                            <td>{payroll.netSalary}</td>
                        </tr>

                    ))}

                </tbody>

            </table>
        </div>
    );
}