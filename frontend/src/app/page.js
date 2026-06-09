export default function MainPage() {
  return (
    <div className="p-4 flex flex-col gap-4 h-full">
      {/* 상단 2열 */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-4 h-1/2">
        <div className="grid grid-rows-2 gap-4">
          <div className="bg-white rounded-xl border border-slate-200 p-4">
            <h1>상단 영역 1</h1>
          </div>
          <div className="bg-white rounded-xl border border-slate-200 p-4">
            <h1>상단 영역 2</h1>
          </div>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-4">
          <h1>상단 영역 3</h1>
        </div>
      </div>

      {/* 하단 2열 */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 h-1/2">
        <div className="bg-white rounded-xl border border-slate-200 p-4">
          <h1>하단 영역 1</h1>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-4">
          <h1>하단 영역 2</h1>
        </div>
      </div>
    </div>
  );
}