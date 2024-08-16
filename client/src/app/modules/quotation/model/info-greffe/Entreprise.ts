import { GreffeImmat } from "./GreffeImmat";

export interface Entreprise {
  denomination: string;
  siren: string;
  greffeImmat: GreffeImmat;
  typeLiasseEDI: null | string;
}
