import { MontantFormalite } from "./MontantFormalite";

export interface RubriqueInfogreffe {
  id: number;
  code: string;
  montantHt: number;
  montantTva: number;
  montantTtc: number;
  montantInfogreffe: MontantFormalite;
}
