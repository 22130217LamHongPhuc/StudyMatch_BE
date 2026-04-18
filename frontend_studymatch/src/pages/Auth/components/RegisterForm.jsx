import InputField from "./InputField";
import Divider from "./Divider";
import SocialAuthGroup from "./SocialLoginGroup";

export default function RegisterForm() {
  const handleSubmit = (e) => {
    e.preventDefault();
  };

  return (
    <>
      <div className="mb-8 text-center md:text-left">
        <h2 className="mb-1 text-2xl font-black tracking-tight text-slate-900">
          Đăng ký tài khoản
        </h2>
        <p className="text-sm font-medium text-slate-500">
          Tham gia cộng đồng StudyMatch ngay hôm nay.
        </p>
      </div>

      <form className="space-y-4" onSubmit={handleSubmit}>
        <InputField
          id="fullName"
          label="Họ và tên"
          placeholder="Nguyễn Văn A"
        />

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <InputField id="studentCode" label="MSSV" placeholder="21110xxx" />
          <InputField
            id="email"
            label="Email sinh viên"
            type="email"
            placeholder="21110xxx@st.hcmuaf.edu.vn"
          />
        </div>

        <InputField
          id="password"
          label="Mật khẩu"
          type="password"
          placeholder="••••••••"
        />

        <InputField
          id="confirmPassword"
          label="Xác nhận mật khẩu"
          type="password"
          placeholder="••••••••"
        />

        <label className="flex items-start gap-3 py-1">
          <div className="flex h-5 items-center">
            <input
              id="terms"
              type="checkbox"
              className="h-4 w-4 rounded border-slate-300 bg-slate-50 text-blue-700 focus:ring-blue-700"
            />
          </div>

          <span className="text-[11px] leading-tight text-slate-500">
            Tôi đồng ý với{" "}
            <a href="#" className="font-bold text-blue-700 hover:underline">
              Điều khoản
            </a>{" "}
            và Chính sách bảo mật.
          </span>
        </label>

        <button
          type="submit"
          className="mt-2 w-full rounded-xl bg-blue-700 py-3.5 text-sm font-bold text-white shadow-lg transition active:scale-[0.98] hover:shadow-blue-700/20"
        >
          Tạo tài khoản
        </button>
      </form>

      <div className="mt-6 space-y-4">
        <Divider text="Hoặc đăng ký với" />
        <SocialAuthGroup actionText="Đăng nhập với" />
      </div>

      <div className="mt-6 text-center">
        <p className="text-xs text-slate-500">
          Đã có tài khoản?
          <a href="#" className="ml-1 font-bold text-blue-700 hover:underline">
            Đăng nhập ngay
          </a>
        </p>
      </div>
    </>
  );
}
