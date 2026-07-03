import Header from "@/components/common/Header";
import PasswordChangeForm from "@/components/user/PasswordChangeForm";

export default function PasswordChangePage() {
  return (
    <>
      <Header title="비밀번호 변경" />

      <div className="w-full bg-slate-100 px-4 py-5 sm:px-6">
        <div className="mx-auto w-full max-w-5xl">
          <PasswordChangeForm />
        </div>
      </div>
    </>
  );
}