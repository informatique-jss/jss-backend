import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { Invoice } from "../../quotation/model/Invoice";

export interface AzureInvoice extends IAttachment {
  id: number;
  modelUsed: string;
  isDisabled: boolean;
  globalDocumentConfidence: number;
  customerId: string;
  reference: string;
  invoiceDate: Date;
  invoiceId: string;
  invoiceTotal: number;
  invoicePreTaxTotal: number;
  invoiceTaxTotal: number;
  invoiceNonTaxableTotal: number;
  vendorTaxId: string;
  customerIdConfidence: number;
  referenceConfidence: number;
  invoiceDateConfidence: number;
  invoiceIdConfidence: number;
  invoiceTotalConfidence: number;
  invoicePreTaxTotalConfidence: number;
  invoiceTaxTotalConfidence: number;
  invoiceNonTaxableTotalConfidence: number;
  vendorTaxIdConfidence: number;
  invoices: Invoice[];
  competentAuthority: CompetentAuthority;
  toCheck: boolean;
}

