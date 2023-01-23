import { Department } from "../../miscellaneous/model/Department";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { Mail } from "../../miscellaneous/model/Mail";
import { PaymentType } from "../../miscellaneous/model/PaymentType";
import { SpecialOffer } from "../../miscellaneous/model/SpecialOffer";
import { VatCollectionType } from "../../miscellaneous/model/VatCollectionType";
import { WeekDay } from "../../miscellaneous/model/WeekDay";
import { ITiers } from "../../tiers/model/ITiers";
import { JournalType } from "./JournalType";

export interface Confrere extends IDocument, ITiers {
  label: string;
  code: string;
  accountingMails: Mail[];
  paymentType: PaymentType;
  paymentIban: string;
  sepaMandateReference: string;
  sepaMandateSignatureDate: Date;
  paymentBic: string;
  isSepaMandateReceived: boolean;
  isProvisionalPaymentMandatory: boolean;
  journalType: JournalType;
  reinvoicing: number;
  weekDays: WeekDay[];
  specialOffers: SpecialOffer[];
  departments: Department[];
  lastShipmentForPublication: string;
  publicationCertificateDocumentGrade: string;
  billingGrade: string;
  paperGrade: string;
  boardGrade: string;
  shippingCosts: number;
  discountRate: number;
  administrativeFees: number;
  numberOfPrint: number;
  paperPrice: number;
  vatCollectionType: VatCollectionType;
}
