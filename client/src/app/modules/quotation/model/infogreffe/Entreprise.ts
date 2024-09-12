import { GreffeInfogreffe } from "./GreffeInfogreffe";

export interface Entreprise {
  denomination: string;
  siren: string;
  greffeInfogreffe: GreffeInfogreffe;
  typeLiasseEDI: string;
}
