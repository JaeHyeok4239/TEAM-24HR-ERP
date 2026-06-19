import Header from '@/components/Header';
import DepartmentManagement from '@/components/hr/reference-data/DepartmentManagement';
import PositionManagement from '@/components/hr/reference-data/PositionManagement';

export default function ReferenceDataPage() {
  return (
    <>
      <Header title="기준정보 관리" />

      <main className="p-6">
        <div className="grid grid-cols-1 gap-6 xl:grid-cols-2">
          <DepartmentManagement />
          <PositionManagement />
        </div>
      </main>
    </>
  );
}