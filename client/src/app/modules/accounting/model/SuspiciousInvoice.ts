export interface SuspiciousInvoiceResult {
  idTiers: number;
  tiers: string;
  idCommercial: number;
  htAmount: number;
  finalAmount: number;
  nbrInvoice: number;
  affaire: string;
  idInvoice: number;
  appliableRate: number;
  suspiciousMarkup: number;
  dueDaysNumber: number;
}
