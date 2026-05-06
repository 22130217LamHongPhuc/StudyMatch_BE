import React from "react";

interface SectionCardProps {
  icon: React.ReactNode;
  title: string;
  children: React.ReactNode;
  description?: string;
}

export default function SectionCard({
  icon,
  title,
  description,
  children,
}: SectionCardProps) {
  return (
    <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-2 flex items-center gap-3">
        <div className="flex size-9 items-center justify-center rounded-xl bg-orange-50 text-orange-600">
          {icon}
        </div>
        <h2 className="text-lg font-semibold text-slate-900 md:text-xl">
          {title}
        </h2>
      </div>

      {description && (
        <p className="mb-6 text-sm leading-relaxed text-slate-600">
          {description}
        </p>
      )}

      {!description && <div className="mb-4" />}

      {children}
    </section>
  );
}
