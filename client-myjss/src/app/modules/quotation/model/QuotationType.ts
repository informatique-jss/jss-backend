export interface QuotationType {
  id: number;
  code: string;
  label: string;
}

export const QUOTATION_TYPE_QUOTATION: QuotationType = { id: 1, code: "QUOTATION", label: "Un devis" };
export const QUOTATION_TYPE_ORDER: QuotationType = { id: 2, code: "ORDER", label: "Une commande" };
