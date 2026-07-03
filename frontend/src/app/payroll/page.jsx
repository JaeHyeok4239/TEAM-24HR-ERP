"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

import PayrollTable from "@/components/payroll/PayrollTable";
import { getPayrolls } from "@/services/payrollService";

export default function PayrollPage() {
  const router = useRouter();

  const [payrolls, setPayrolls] = useState([]);

  const [month, setMonth] = useState("");
  const [employeeNo, setEmployeeNo] = useState("");
  const [departmentId, setDepartmentId] = useState("");

  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(0);

  const [totalElements, setTotalElements] = useState(0);

  const loadPayrolls = async () => {
    try {
      const data = await getPayrolls({
        month,
        employeeNo,
        departmentId,
        page,
        size: 10,
      });

      setPayrolls(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (error) {
      console.error(error);
      alert("급여 목록 조회 실패");
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        await loadPayrolls();
      } catch (error) {
        console.error(error);
      }
    };

    fetchData();
  }, [page]);

  const handleSearch = () => {
    setPage(0);

    if (page === 0) {
      loadPayrolls();
    }
  };

  return (
    <div className="p-6">

      <h1 className="text-2xl font-bold mb-6">
        급여대장
      </h1>

      <div className="flex gap-3 mb-5">

        <input
          type="month"
          value={month}
          onChange={(e) =>
            setMonth(e.target.value)
          }
          className="border p-2 rounded"
        />

        <input
          type="text"
          placeholder="사번"
          value={employeeNo}
          onChange={(e) =>
            setEmployeeNo(e.target.value)
          }
          className="border p-2 rounded"
        />

        <input
          type="number"
          placeholder="부서ID"
          value={departmentId}
          onChange={(e) =>
            setDepartmentId(e.target.value)
          }
          className="border p-2 rounded"
        />

        <button
          onClick={handleSearch}
          className="bg-blue-600 text-white px-4 rounded"
        >
          조회
        </button>

      </div>

      {/* 테이블 */}

      <PayrollTable
        payrolls={payrolls}
        onRowClick={(payrollId) =>
          router.push(
            `/payroll/${payrollId}`
          )
        }
      />

      {/* 페이징 */}

      <div className="flex justify-center items-center gap-2 mt-5">

        <button
          disabled={page === 0}
          onClick={() => setPage(prev => prev - 1)}
          className="border px-3 py-1 rounded disabled:opacity-50"
        >
          이전
        </button>

        {Array.from({ length: totalPages }, (_, index) => (
          <button
            key={index}
            onClick={() => setPage(index)}
            className={
              page === index
                ? "bg-blue-600 text-white px-3 py-1 rounded"
                : "border px-3 py-1 rounded"
            }
          >
            {index + 1}
          </button>
        ))}

        <button
          disabled={page + 1 >= totalPages}
          onClick={() => setPage(prev => prev + 1)}
          className="border px-3 py-1 rounded disabled:opacity-50"
        >
          다음
        </button>

      </div>

    </div>
  );
}