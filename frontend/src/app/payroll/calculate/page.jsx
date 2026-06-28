"use client";

import { useEffect, useState } from "react";

import {calculatePayroll,} from "@/services/payrollService";

import {getHrEmployeesRequest,} from "@/services/hrEmployeeService";

export default function PayrollCalculatePage() {

  const [users, setUsers] = useState([]);

  const [employeeId, setEmployeeId] = useState("");

  const [payMonth, setPayMonth] = useState("");

  const [loading, setLoading] = useState(false);

  // 직원 목록 조회
  useEffect(() => {

    const loadUsers = async () => {

      try {

        const data = await getHrEmployeesRequest();

        setUsers(data);

      } catch (e) {

        console.error(e);

        alert("직원 목록 조회 실패");

      }

    };

    loadUsers();

  }, []);

  const handleCalculate = async () => {

    if (!employeeId) {

      alert("직원을 선택하세요.");

      return;

    }

    if (!payMonth) {

      alert("급여월을 선택하세요.");

      return;

    }

    try {

      setLoading(true);

      await calculatePayroll({

        employeeId,

        payMonth,

      });

      alert("급여 계산이 완료되었습니다.");

    } catch (e) {

      console.error(e);

      alert("급여 계산 실패");

    } finally {

      setLoading(false);

    }

  };

  return (

    <div className="p-6 max-w-xl">

      <h1 className="text-2xl font-bold mb-6">

        급여 계산

      </h1>

      <div className="bg-white border rounded-lg p-6 space-y-5">

        <div>

          <label className="block mb-2 font-medium">

            직원

          </label>

          <select
            value={employeeId}
            onChange={(e)=>setEmployeeId(e.target.value)}
            className="border rounded p-2 w-full"
          >

            <option value="">
              선택하세요
            </option>

            {users.map(user => (

              <option
                key={user.employeeId}
                value={user.employeeId}
              >

                {user.employeeNo}
                {" - "}
                {user.name}

              </option>

            ))}

          </select>

        </div>

        <div>

          <label className="block mb-2 font-medium">

            급여월

          </label>

          <input
            type="month"
            value={payMonth}
            onChange={(e)=>setPayMonth(e.target.value)}
            className="border rounded p-2 w-full"
          />

        </div>

        <button

          onClick={handleCalculate}

          disabled={loading}

          className="bg-blue-600 text-white px-6 py-2 rounded"

        >

          {loading ? "계산중..." : "급여 계산"}

        </button>

      </div>

    </div>

  );

}