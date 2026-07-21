import type { ReactNode } from "react";
import { Link } from "../../router/Router";

export function CatalogCard({
  title,
  meta,
  href,
  cta,
}: {
  title: string;
  meta?: ReactNode;
  href: string;
  cta: string;
}) {
  return (
    <li className="catalog-card">
      <h3 className="catalog-card__title">{title}</h3>
      {meta && <div className="catalog-card__meta">{meta}</div>}
      <Link to={href} className="catalog-card__cta">
        {cta}
      </Link>
    </li>
  );
}
