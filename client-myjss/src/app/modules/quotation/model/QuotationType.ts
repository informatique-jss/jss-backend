export interface QuotationType {
  id: number;
  code: string;
  label: string;
}

export const quotation: QuotationType = { id: 1, code: "QUOTATION", label: "Un devis" };
export const order: QuotationType = { id: 2, code: "ORDER", label: "Une commande" };
