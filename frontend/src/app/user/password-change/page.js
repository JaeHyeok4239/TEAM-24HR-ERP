import Header from '@/components/Header';
import PasswordChangeForm from '@/components/user/PasswordChangeForm';

export default function PasswordChangePage() {
  return (
    <>
      <Header title="비밀번호 변경" />
      <PasswordChangeForm />
    </>
  );
}