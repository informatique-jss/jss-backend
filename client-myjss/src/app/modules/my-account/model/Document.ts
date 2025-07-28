import { Mail } from "../../general/model/Mail";
import { City } from "../../profile/model/City";
import { Country } from "../../profile/model/Country";
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
  billingLabel: string;
  billingAddress: string;
  billingPostalCode: string;
  cedexComplement: string;
  billingLabelCity: City;
  billingLabelCountry: Country;
  billingLabelIsIndividual: boolean;
  isCommandNumberMandatory: boolean;
  commandNumber: string;
  externalReference: string;
  clientAddress: string;
  affaireAddress: string;
  clientRecipient: string;
  affaireRecipient: string;
}
