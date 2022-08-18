import { JournalType } from "./JournalType";

export interface ConfrereFlatten {
  id: number;
  denomination: string;
  departments: string;
  mails: string;
  phones: string;
  discountRate: string;
  reinvoicing: number;
  weekDays: string;
  journalType: JournalType;
  lastShipmentForPublication: string;
  publicationCertificateDocumentGrade: string;
  billingGrade: string;
  paperGrade: string;
  boardGrade: string;
  shippingCosts: number;
  administrativeFees: number;
  numberOfPrint: number;
}
