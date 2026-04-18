export default function Divider() {
  return (
    <div className="relative my-8">
      <div className="absolute inset-0 flex items-center">
        <div className="w-full border-t border-slate-200" />
      </div>
      <div className="relative flex justify-center text-sm">
        <span className="bg-white px-4 italic text-slate-400">
          Hoặc tiếp tục với
        </span>
      </div>
    </div>
  );
}
