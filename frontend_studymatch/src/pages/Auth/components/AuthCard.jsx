export default function AuthCard({ children, className = "" }) {
  return (
    <div
      className={`relative z-10 overflow-hidden rounded-3xl border border-white/20 bg-white/95 shadow-2xl backdrop-blur-xl ${className}`}
    >
      {children}
    </div>
  );
}
