export default function Footer() {
  return (
    <footer className="relative z-10 mt-auto w-full border-t border-slate-200/80 bg-slate-100/95">
      <div className="mx-auto flex max-w-screen-2xl flex-col items-center justify-between gap-4 px-8 py-8 md:flex-row">
        <div className="flex flex-col items-center gap-1 md:items-start">
          <span className="text-lg font-bold text-blue-700">StudyMatch</span>
          <p className="text-center text-xs text-slate-500 md:text-left">
            © 2024 StudyMatch AI. Nền tảng kết nối học tập thông minh.
          </p>
        </div>

        <nav className="flex flex-wrap justify-center gap-6">
          <a
            href="#"
            className="text-xs font-medium text-slate-600 transition hover:text-blue-700"
          >
            Chính sách bảo mật
          </a>
          <a
            href="#"
            className="text-xs font-medium text-slate-600 transition hover:text-blue-700"
          >
            Điều khoản sử dụng
          </a>
          <a
            href="#"
            className="text-xs font-medium text-slate-600 transition hover:text-blue-700"
          >
            Trung tâm trợ giúp
          </a>
        </nav>
      </div>
    </footer>
  );
}
