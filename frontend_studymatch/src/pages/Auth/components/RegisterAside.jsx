export default function RegisterAside() {
  return (
    <div className="hidden w-full flex-col justify-between bg-blue-700 p-8 text-white md:flex md:w-1/3">
      <div>
        <div className="mb-6 flex h-12 w-12 items-center justify-center rounded-2xl bg-white/20 backdrop-blur-sm">
          <span className="text-xl">🎓</span>
        </div>

        <h3 className="mb-2 text-xl font-black leading-tight">
          Đại học Nông Lâm TP.HCM
        </h3>
        <p className="text-sm text-white/80">
          Cộng đồng học tập năng động dành riêng cho sinh viên NLU.
        </p>
      </div>

      <div className="space-y-4">
        <div className="rounded-xl border border-white/10 bg-white/10 p-4 backdrop-blur-sm">
          <span className="block text-xl font-bold">500+</span>
          <span className="text-[10px] font-semibold uppercase tracking-wider opacity-70">
            Nhóm học tập
          </span>
        </div>

        <div className="rounded-xl border border-white/10 bg-white/10 p-4 backdrop-blur-sm">
          <span className="block text-xl font-bold">98%</span>
          <span className="text-[10px] font-semibold uppercase tracking-wider opacity-70">
            Tỉ lệ kết nối
          </span>
        </div>
      </div>
    </div>
  );
}
