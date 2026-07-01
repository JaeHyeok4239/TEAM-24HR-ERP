"use client";

import { useEffect, useState } from "react";

import {
  ResponsiveContainer,
  LineChart,
  Line,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip,  
} from "recharts";

import {getMonthlyCost,} from "@/services/payrollDashboardService";

export default function PayrollDashboardPage() {

  const [monthlyData, setMonthlyData] = useState([]);  

  const [loading, setLoading] = useState(true);

  useEffect(() => {

    const loadStatistics = async () => {

      try {

        const [monthly,] = await Promise.all([getMonthlyCost(),]);

        setMonthlyData(monthly);        

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

      {/* 월별 인건비 */}

      <div className="bg-white rounded-lg shadow p-6">

        <h2 className="text-lg font-semibold mb-4">

          월별 인건비 추이

        </h2>

        <ResponsiveContainer width="100%" height={350}>

          <LineChart data={monthlyData}>

            <CartesianGrid strokeDasharray="3 3" />

            <XAxis dataKey="month" />

            <YAxis
                width={100}
                tickFormatter={(value) => Number(value).toLocaleString("ko-KR")}
            />

            <Tooltip
              formatter={(value) => `${Number(value).toLocaleString("ko-KR")}원`}
            />

            <Line
              type="monotone"
              dataKey="totalCost"
              stroke="#2563eb"
              strokeWidth={3}
            />

          </LineChart>

        </ResponsiveContainer>

      </div>
      
    </div>

  );

}