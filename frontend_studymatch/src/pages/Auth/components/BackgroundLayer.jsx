export default function BackgroundLayer() {
  return (
    <div className="absolute inset-0 z-0">
      <img
        src="https://reviewedu.net/wp-content/uploads/2021/10/dai-hoc-nong-lam-tphcm-3.jpg"
        alt="Sinh viên Việt Nam học tập cùng nhau"
        className="h-full w-full object-cover"
      />
      <div className="absolute inset-0 bg-blue-700/30 backdrop-blur-[2px]" />
    </div>
  );
}
