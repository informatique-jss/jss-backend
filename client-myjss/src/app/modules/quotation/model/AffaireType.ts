export interface AffaireType {
  id: number;
  code: string;
  label: string;
}

export const notIndividual: AffaireType = { id: 1, code: "NOT_INDIVIDUAL", label: "Créer une société" };
export const individual: AffaireType = { id: 2, code: "INDIVIDUAL", label: "Créer une entreprise en nom propre" };
