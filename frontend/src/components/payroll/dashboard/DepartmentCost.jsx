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

import {getDepartmentCost,} from "@/services/payrollDashboardService";

export default function PayrollDashboardPage() {

  const [departmentData, setDepartmentData] = useState([]);

  const [loading, setLoading] = useState(true);

  useEffect(() => {

    const loadStatistics = async () => {

      try {

        const [department,] = await Promise.all([getDepartmentCost(),]);        

        setDepartmentData(department);

      } catch (error) {

        console.error(error);

        alert("통계 조회 실패");

      } finally {

        setLoading(false);

      }

    };

    loadStatistics();

  }, []);

  if (loading) {

    return (
      <div className="p-6">
        로딩중...
      </div>
    );

  }

  return (

    <div className="p-6 space-y-8">            

      {/* 부서별 인건비 */}

      <div className="bg-white rounded-lg shadow p-6">

        <h2 className="text-lg font-semibold mb-4">

          부서별 인건비

        </h2>

        <ResponsiveContainer width="100%" height={350}>

          <BarChart data={departmentData}>

            <CartesianGrid strokeDasharray="3 3" />

            <XAxis dataKey="departmentName" />

            <YAxis
                width={100}
                tickFormatter={(value) => Number(value).toLocaleString("ko-KR")}
            />

            <Tooltip
              formatter={(value) => `${Number(value).toLocaleString("ko-KR")}원`}
            />

            <Bar
              dataKey="totalCost"
              fill="#16a34a"
            />

          </BarChart>

        </ResponsiveContainer>

      </div>

    </div>

  );

}