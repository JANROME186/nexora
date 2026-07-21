/**
 * Deployment-owned identity for this public website instance. The `/api/public/**` surface has no
 * public branch/location directory (COM-MOD-011-DEF modeled no BCM-ORG-* capability), so a public
 * site is deployed per laboratory network and its tenant/laboratory identity is configuration, not
 * something discovered from an API at runtime. Override via Vite env vars per deployment; the
 * fallbacks match the local-solution seed fixtures (see the backend module schema.sql files and
 * HopSecurityProperties local-fixture defaults) so the local demo works out of the box.
 */
export interface SiteBranch {
  branchId: string;
  nameEn: string;
  nameEs: string;
  addressEn: string;
  addressEs: string;
  phone: string;
}

export interface SiteConfig {
  tenantId: string;
  laboratoryId: string;
  branches: SiteBranch[];
}

const DEFAULT_TENANT_ID = "tenant-local";
const DEFAULT_LABORATORY_ID = "lab-local";
const DEFAULT_BRANCH_ID = "branch-local";

function readEnv(key: string, fallback: string): string {
  const value = import.meta.env[key];
  return typeof value === "string" && value.length > 0 ? value : fallback;
}

export const siteConfig: SiteConfig = {
  tenantId: readEnv("VITE_TENANT_ID", DEFAULT_TENANT_ID),
  laboratoryId: readEnv("VITE_LABORATORY_ID", DEFAULT_LABORATORY_ID),
  branches: [
    {
      branchId: readEnv("VITE_DEFAULT_BRANCH_ID", DEFAULT_BRANCH_ID),
      nameEn: "Main Laboratory Branch",
      nameEs: "Sucursal Principal del Laboratorio",
      addressEn: "Configure this branch's public address in site configuration.",
      addressEs: "Configure la dirección pública de esta sucursal en la configuración del sitio.",
      phone: "",
    },
  ],
};
