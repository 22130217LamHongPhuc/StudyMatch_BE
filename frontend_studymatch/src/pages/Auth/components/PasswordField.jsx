export default function PasswordField() {
  return (
    <div>
      <div className="mb-2 ml-1 flex items-center justify-between gap-3">
        <label
          htmlFor="password"
          className="block text-sm font-semibold text-slate-800"
        >
          Mật khẩu
        </label>
        <a
          href="#"
          className="text-sm font-semibold text-blue-700 transition hover:underline"
        >
          Quên mật khẩu?
        </a>
      </div>

      <input
        id="password"
        name="password"
        type="password"
        placeholder="••••••••"
        className="w-full rounded-xl border border-slate-200 bg-slate-50 px-5 py-4 text-slate-800 outline-none transition focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20"
      />
    </div>
  );
}
