import BackgroundLayer from "./components/BackgroundLayer";
import LoginCard from "./components/LoginCard";
import Footer from "./../../components/Footer/Footer";

export default function LoginPage() {
  return (
    <div className="relative flex min-h-screen flex-col overflow-hidden bg-slate-50 font-sans text-slate-900">
      <BackgroundLayer />

      <main className="relative z-10 flex min-h-screen flex-1 items-center justify-center px-4 py-6">
        <div className="w-full max-w-lg">
          <LoginCard />
        </div>
      </main>

      <Footer />
    </div>
  );
}
