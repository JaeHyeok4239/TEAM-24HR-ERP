import Header from "@/components/common/Header";
import DepartmentManagement from "@/components/hr/reference-data/DepartmentManagement";
import PositionManagement from "@/components/hr/reference-data/PositionManagement";

export default function ReferenceDataPage() {
  return (
    <>
      <Header title="기준정보 관리" />

      <main className="w-full bg-slate-100 px-4 py-5 sm:px-6">
        <div className="mx-auto w-full max-w-[1650px] space-y-5">
          <div className="grid grid-cols-1 gap-5 xl:grid-cols-2">
            <DepartmentManagement />
            <PositionManagement />
          </div>
        </div>
      </main>
    </>
  );
}
