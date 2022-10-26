import { AccountingJournal } from "../../accounting/model/AccountingJournal";
import { BillingLabelType } from "../../tiers/model/BillingLabelType";
import { TiersType } from "../../tiers/model/TiersType";
import { AttachmentType } from "./AttachmentType";
import { BillingType } from "./BillingType";
import { Country } from "./Country";
import { DocumentType } from "./DocumentType";

export interface Constant {
  id: number;
  billingLabelTypeCodeAffaire: BillingLabelType;
  billingLabelTypeOther: BillingLabelType;
  billingLabelTypeCustomer: BillingLabelType;
  accountingJournalSales: AccountingJournal;
  accountingJournalPurchases: AccountingJournal;
  accountingJournalANouveau: AccountingJournal;
  tiersTypeProspect: TiersType;
  documentTypePublication: DocumentType;
  documentTypeCfe: DocumentType;
  documentTypeKbis: DocumentType;
  documentTypeBilling: DocumentType;
  documentTypeDunning: DocumentType;
  documentTypeRefund: DocumentType;
  documentTypeBillingClosure: DocumentType;
  documentTypeProvisionnalReceipt: DocumentType;
  documentTypeProofReading: DocumentType;
  documentTypePublicationCertificate: DocumentType;
  documentTypeQuotation: DocumentType;
  attachmentTypeKbis: AttachmentType;
  attachmentTypeCni: AttachmentType;
  attachmentTypeLogo: AttachmentType;
  attachmentTypeProofOfAddress: AttachmentType;
  countryFrance: Country;
  countryMonaco: Country;
  billingTypeLogo: BillingType;
}
