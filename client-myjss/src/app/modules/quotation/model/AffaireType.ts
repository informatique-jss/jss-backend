export interface AffaireType {
  id: number;
  code: string;
  label: string;
}

export const notIndividual: AffaireType = { id: 1, code: "NOT_INDIVIDUAL", label: "Société" };
export const individual: AffaireType = { id: 2, code: "INDIVIDUAL", label: "Personne physique" };
