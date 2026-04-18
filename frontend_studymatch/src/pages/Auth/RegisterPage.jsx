import AuthShell from "./components/AuthShell";
import AuthCard from "./components/AuthCard";
import RegisterAside from "./components/RegisterAside";
import RegisterForm from "./components/RegisterForm";

export default function RegisterPage() {
  return (
    <AuthShell
      showHeader
      headerAction={
        <a
          href="/login"
          className="text-sm font-bold text-blue-700 transition hover:opacity-80 md:text-base"
        >
          Đăng nhập
        </a>
      }
    >
      <main className="relative flex flex-1 items-center justify-center overflow-hidden p-4">
        <div className="absolute left-[-3rem] top-1/4 h-64 w-64 rounded-full bg-blue-700/20 blur-3xl" />
        <div className="absolute bottom-1/4 right-[-3rem] h-64 w-64 rounded-full bg-emerald-300/20 blur-3xl" />

        <div className="relative z-10 w-full max-w-2xl">
          <AuthCard className="flex flex-col md:flex-row">
            <RegisterAside />

            <div className="w-full bg-white p-8 md:w-2/3 md:p-10 lg:p-12">
              <RegisterForm />
            </div>
          </AuthCard>
        </div>
      </main>
    </AuthShell>
  );
}
