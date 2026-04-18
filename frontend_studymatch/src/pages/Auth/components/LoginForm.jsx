import InputField from "./InputField";
import PasswordField from "./PasswordField";

export default function LoginForm() {
  const handleSubmit = (e) => {
    e.preventDefault();
  };

  return (
    <form className="space-y-5" onSubmit={handleSubmit}>
      <InputField
        id="email"
        label="Email / MSSV"
        placeholder="sv123456@university.edu.vn"
      />

      <PasswordField />

      <label className="flex cursor-pointer items-center">
        <input
          type="checkbox"
          className="h-5 w-5 rounded border-slate-300 text-blue-700 focus:ring-blue-700/20"
        />
        <span className="ml-3 text-sm font-medium text-slate-500">
          Ghi nhớ đăng nhập
        </span>
      </label>

      <button
        type="submit"
        className="w-full rounded-xl bg-blue-700 py-4 text-lg font-bold text-white shadow-lg shadow-blue-700/20 transition duration-200 hover:scale-[1.01] hover:bg-blue-800 active:scale-[0.99]"
      >
        Đăng nhập
      </button>
    </form>
  );
}
