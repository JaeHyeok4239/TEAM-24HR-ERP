import Header from '@/components/Header';
import DepartmentManagement from '@/components/hr/reference-data/DepartmentManagement';

export default function ReferenceDataPage() {
  return (
    <>
      <Header title="기준정보 관리" />

      <main className="p-6">
        <div className="grid grid-cols-1 gap-6 xl:grid-cols-2">
          <DepartmentManagement />

          <section className="rounded-md border bg-white p-5">
            <h2 className="font-semibold text-slate-900">
              직급 관리
            </h2>

            <p className="mt-4 text-sm text-slate-500">
              미완성
            </p>
          </section>
        </div>
      </main>
    </>
  );
}