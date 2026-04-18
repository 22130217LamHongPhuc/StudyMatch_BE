import BackgroundLayer from "./BackgroundLayer";
import Footer from "../../../components/Footer/Footer";

export default function AuthShell({
  children,
  showHeader = false,
  headerAction,
}) {
  return (
    <div className="relative flex min-h-screen flex-col overflow-hidden bg-slate-50 font-sans text-slate-900">
      {showHeader && (
        <header className="relative z-20  top-0 border-b border-slate-200 bg-white/80 backdrop-blur-md">
          <nav className="mx-auto flex w-full max-w-screen-2xl items-center justify-between px-6 py-4">
            <span className="text-2xl font-black tracking-tight text-blue-700">
              StudyMatch
            </span>
            {headerAction}
          </nav>
        </header>
      )}

      <BackgroundLayer />
      {children}
      <Footer />
    </div>
  );
}
