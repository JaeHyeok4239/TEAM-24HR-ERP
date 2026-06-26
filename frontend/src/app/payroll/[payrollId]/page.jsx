"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";

import { getPayrollDetail } from "@/services/payrollService";

export default function PayrollDetailPage() {

  const { payrollId } = useParams();

  const [detail, setDetail] = useState(null);

  const getAmount = (itemName) => {
    const item = detail.details?.find(
      (d) => d.itemName === itemName
    );

    return item ? item.amount : 0;
  };

  useEffect(() => {

    const loadDetail = async () => {

      try {

        const data = await getPayrollDetail(payrollId);

        setDetail(data);

      } catch (error) {

        console.error(error);

        alert("급여 상세 조회 실패");
      }
    };

    if (payrollId) { loadDetail(); }

  }, [payrollId]);

  if (!detail) { return <div>로딩중...</div>; }

  return (
    <div className="p-6">

      <h1 className="text-2xl font-bold mb-6">
        급여 상세조회
      </h1>

      <div className="border rounded p-6 bg-white">

        <h2 className="text-lg font-semibold mb-4">
          기본 정보
        </h2>

        <p>사번 : {detail.employeeNo}</p>
        <p>이름 : {detail.employeeName}</p>
        <p>부서 : {detail.departmentName}</p>
        <p>급여월 : {detail.payMonth}</p>

        <hr className="my-5" />

        <h2 className="font-semibold mb-3">
          지급 항목
        </h2>

        <p>기본급 : {detail.baseSalary?.toLocaleString()}원</p>
        <p>직책수당 : {getAmount("직책수당").toLocaleString()}원</p>
        <p>식대 : {getAmount("식대").toLocaleString()}원</p>
        <p>교통비 : {getAmount("교통비").toLocaleString()}원</p>
        <p>초과근무수당 : {getAmount("초과근무수당").toLocaleString()}원</p>

        <hr className="my-5" />

        <h2 className="font-semibold mb-3">
          공제 항목
        </h2>

        <p>국민연금 : {getAmount("국민연금").toLocaleString()}원</p>
        <p>건강보험 : {getAmount("건강보험").toLocaleString()}원</p>
        <p>고용보험 : {getAmount("고용보험").toLocaleString()}원</p>
        <p>소득세 : {getAmount("소득세").toLocaleString()}원</p>
        <p>지방소득세 : {getAmount("지방소득세").toLocaleString()}원</p>

        <hr className="my-5" />

        <p>
          총지급액 :
          <strong>
            {" "}
            {detail.totalPay?.toLocaleString()}원
          </strong>
        </p>

        <p>
          총공제액 :
          <strong>
            {" "}
            {detail.totalDeduction?.toLocaleString()}원
          </strong>
        </p>

        <p>
          실수령액 :
          <strong className="text-blue-600">
            {" "}
            {detail.netSalary?.toLocaleString()}원
          </strong>
        </p>

      </div>
    </div>
  );
}