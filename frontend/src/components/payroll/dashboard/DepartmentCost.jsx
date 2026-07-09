"use client";

import { useEffect, useState } from "react";

import {
  ResponsiveContainer,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip,
  BarChart,
  Bar,
} from "recharts";

import { getDepartmentCost } from "@/services/payrollDashboardService";

export default function DepartmentCost() {

  // 현재 날짜를 기준으로 기본 조회월 설정
  const today = new Date();

  const currentMonth =
    `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, "0")}`;

  const [month, setMonth] = useState(currentMonth);

  const [departmentData, setDepartmentData] = useState([]);

  const [loading, setLoading] = useState(false);

  // 통계 조회 함수
  const loadStatistics = async () => {

    try {

      setLoading(true);

      const department = await getDepartmentCost(month);

      setDepartmentData(department);

    } catch (error) {

      console.error(error);

      alert("부서별 인건비 조회 실패");

    } finally {

      setLoading(false);

    }

  };

  // 처음 화면 진입 시 자동 조회
  useEffect(() => {

    loadStatistics();

  }, []);

  return (

    <div className="p-6 space-y-6">

      {/* 조회 조건 */}
      <div className="bg-white rounded-lg shadow p-4 flex items-center gap-3">

        <label className="font-semibold">

          조회월

        </label>

        <input
          type="month"
          value={month}
          onChange={(e) => setMonth(e.target.value)}
          className="border rounded px-3 py-2"
        />

        <button
          onClick={loadStatistics}
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          조회
        </button>

      </div>

      {/* 차트 */}
      <div className="bg-white rounded-lg shadow p-6">

        <h2 className="text-lg font-semibold mb-4">

          부서별 인건비

        </h2>

        {loading ? (

          <div className="text-center py-20">

            로딩중...

          </div>

        ) : (

          <ResponsiveContainer
            width="100%"
            height={350}
          >

            <BarChart data={departmentData}>

              <CartesianGrid strokeDasharray="3 3" />

              <XAxis
                dataKey="departmentName"
                angle={-45}
                textAnchor="end"
                interval={0}
                height={80}
              />

              <YAxis
                width={120}
                tickFormatter={(value) =>
                  Number(value).toLocaleString("ko-KR")
                }
              />

              <Tooltip
                formatter={(value) =>
                  `${Number(value).toLocaleString("ko-KR")}원`
                }
              />

              <Bar
                dataKey="totalCost"
                fill="#16a34a"
              />

            </BarChart>

          </ResponsiveContainer>

        )}

      </div>

    </div>

  );

}