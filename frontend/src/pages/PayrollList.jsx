import { useEffect, useState } from "react";
import axios from "axios";

function PayrollList() {

    const [payrolls, setPayrolls] = useState([]);

    useEffect(() => {

        const fetchPayrolls = async () => {

            const response = await axios.get(
                "http://localhost:8080/api/payrolls"
            );

            setPayrolls(response.data);

        };

        fetchPayrolls();

    }, []);

    return (
        <table border="1">

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
    );
}

export default PayrollList;