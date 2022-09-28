import { Department } from "../../miscellaneous/model/Department";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { Mail } from "../../miscellaneous/model/Mail";
import { PaymentType } from "../../miscellaneous/model/PaymentType";
import { SpecialOffer } from "../../miscellaneous/model/SpecialOffer";
import { VatCollectionType } from "../../miscellaneous/model/VatCollectionType";
import { WeekDay } from "../../miscellaneous/model/WeekDay";
import { ITiers } from "../../tiers/model/ITiers";
import { JournalType } from "./JournalType";
import { Regie } from "./Regie";

export interface Confrere extends IDocument, ITiers {
  label: string;
  code: string;
  accountingMails: Mail[];
  iban: string;
  paymentType: PaymentType;
  paymentBIC: string;
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
  administrativeFees: number;
  numberOfPrint: number;
  paperPrice: number;
  regie: Regie;
  vatCollectionType: VatCollectionType;
}
