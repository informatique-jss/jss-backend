export interface QuotationReporting {
  affaireId: number;
  affaireLabel: string;
  customerOrderId: number;
  provisionId: number;
  provisionTypeLabel: string;
  provisionFamilyTypeLabel: string;
  customerOrderLabel: string;
  tiersLabel: string;
  preTaxPrice: number;
  taxedPrice: number;
}
