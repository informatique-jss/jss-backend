import { Mail } from "../../profile/model/Mail";
import { BillingLabelType } from "./BillingLabelType";
import { DocumentType } from "./DocumentType";


export interface Document {
  id: number | undefined;
  code: string;
  documentType: DocumentType;
  isRecipientClient: boolean;
  isRecipientAffaire: boolean;
  mailsClient: Mail[];
  addToClientMailList: boolean;
  mailsAffaire: Mail[];
  addToAffaireMailList: boolean;
  billingLabelType: BillingLabelType;
  isCommandNumberMandatory: boolean;
  commandNumber: string;
  externalReference: string;
}
