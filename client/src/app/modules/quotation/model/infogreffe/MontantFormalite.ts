import { RubriqueInfogreffe } from "./RubriqueInfogreffe";

export interface MontantFormalite {
  id: number;
  montantTotal: number;
  typePaiement: string;
  rubriques: RubriqueInfogreffe[];
}
