import Header from "@/components/common/Header";
import MyInfoForm from "@/components/user/my-info/MyInfoForm";

export default function MyInfoPage() {
  return (
    <>
      <Header title="내 정보 수정" />

      <div className="w-full bg-slate-100 px-6 py-6">
        <div className="mx-auto max-w-6xl">
          <MyInfoForm />
        </div>
      </div>
    </>
  );
}
