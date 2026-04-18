const socialButtons = [
  {
    id: "google",
    label: "Google",
    icon: "https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg",
  },
  {
    id: "facebook",
    label: "Facebook",
    icon: "https://upload.wikimedia.org/wikipedia/commons/5/51/Facebook_f_logo_%282019%29.svg",
  },
];

export default function SocialLoginGroup() {
  return (
    <div className="grid grid-cols-2 gap-4">
      {socialButtons.map((button) => (
        <button
          key={button.id}
          type="button"
          className="flex items-center justify-center gap-3 rounded-xl border border-slate-200 px-4 py-3 transition hover:bg-slate-50"
        >
          <img
            src={button.icon}
            alt={button.label}
            className="h-5 w-5 object-contain"
          />
          <span className="text-sm font-bold text-slate-800">
            {button.label}
          </span>
        </button>
      ))}
    </div>
  );
}
