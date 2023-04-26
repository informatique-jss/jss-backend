import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";

export interface InfogreffeInvoice {
  id: number;
  invoiceNumber: string;
  competentAuthority: CompetentAuthority;
  productLabel: string;
  invoiceDateTime: Date;
  preTaxPrice: string;
  vatPrice: string;
  customerReference: string;
  sirenAffaire: string;
}

