"use client";

import { useEffect, useState } from "react";

import {
  getSalaryList,
  createSalary,
  updateSalary,
} from "@/services/salaryService";

export default function SalaryPage() {

  const [salaryList, setSalaryList] = useState([]);

  const [loading, setLoading] = useState(true);

  useEffect(() => {

    const fetchData = async () => {

      try {

        const data = await getSalaryList();

        setSalaryList(data);

      } catch (e) {console.error(e);}

      finally {setLoading(false);}
    };

    fetchData();

  }, []);

  const handleSalaryChange = (employeeId, value) => {

    setSalaryList((prev) =>
      prev.map((item) =>
        item.employeeId === employeeId
          ? {
              ...item,
              baseSalary: value,
            }
          : item
      )
    );
  };

  const handleSave = async (salary) => {

    try {

      if (!salary.baseSalary || salary.baseSalary <= 0) {
        alert("기본급을 입력하세요.");
        return;
      }

      // 등록
      if (salary.salaryId == null) {

        await createSalary({

          employeeId: salary.employeeId,

          baseSalary: Number(salary.baseSalary),

        });

        alert("기본급 등록 완료");

      }

      // 수정
      else {

        await updateSalary(

          salary.employeeId,

          {baseSalary: Number(salary.baseSalary),}

        );

        alert("기본급 수정 완료");

      }

      const data = await getSalaryList();
      setSalaryList(data);

    } catch (error) {

      console.error(error);

      alert("저장 실패");

    }

  };

  if (loading) {

    return <div className="p-6">로딩중...</div>;

  }

  return (

    <div className="p-6">

      <h1 className="text-2xl font-bold mb-6">

        기본급 관리

      </h1>

      <div className="bg-white rounded-lg shadow">

        <table className="w-full border-collapse">

          <thead>

            <tr className="bg-gray-100">

              <th className="border p-3">사번</th>

              <th className="border p-3">이름</th>

              <th className="border p-3">부서</th>

              <th className="border p-3">기본급</th>

              <th className="border p-3">관리</th>

            </tr>

          </thead>

          <tbody>

            {

              salaryList.map((salary) => (

                <tr key={salary.employeeId}>

                  <td className="border p-3">

                    {salary.employeeNo}

                  </td>

                  <td className="border p-3">

                    {salary.employeeName}

                  </td>

                  <td className="border p-3">

                    {salary.departmentName}

                  </td>

                  <td className="border p-3">

                    <input

                      type="number"

                      value={salary.baseSalary ?? ""}

                      onChange={(e) =>
                        handleSalaryChange(
                          salary.employeeId,
                          e.target.value
                        )
                      }

                      className="border rounded p-2 w-40"

                    />

                  </td>

                  <td className="border p-3">

                    <button

                      onClick={() => handleSave(salary)}

                      className={
                        salary.salaryId == null
                          ? "bg-blue-600 text-white px-4 py-2 rounded"
                          : "bg-green-600 text-white px-4 py-2 rounded"
                      }

                    >

                      {

                        salary.salaryId == null

                          ? "등록"

                          : "수정"

                      }

                    </button>

                  </td>

                </tr>

              ))

            }

          </tbody>

        </table>

      </div>

    </div>

  );

}