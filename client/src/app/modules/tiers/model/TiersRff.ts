import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";
import { Responsable } from "./Responsable";

export interface TiersRff {
  denomination:  string | null;
  address: string;
  id: number;
  mails: Mail[];
  phones: Phone[];
  lastName: string;
  firstName: string;
  responsables: Responsable[];
  turnoverAmountWithTax: number;
  turnoverAmountWithTaxLastYear: number;
  announcementNbr: number;
  formalityNbr: number;
  announcementNbrLastYear: number;
  formalityNbrLastYear: number;
  customerOrderNbr: number;
  customerOrderNbrLastYear: number;
  remainingToPayInvoice: number;
}
