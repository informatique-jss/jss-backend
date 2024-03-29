import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Responsable } from "./Responsable";

export interface ResponsablesRff {
  id: number;
  name: string | null;
  responsables: Responsable[];
  function: string;
  mails: Mail[];
  phones: Phone[];
  turnoverAmountWithTax: number;
  turnoverAmountWithTaxLastYear: number;
  announcementNbr: number;
  announcementNbrLastYear: number;
  formalityNbr: number;
  formalityNbrLastYear: number;
  totalCustomerOrders: number;
  isSub: boolean;
  gift: boolean;
  others: string;
  customerOrders: CustomerOrder[];
  rffInsertion: number | undefined;
  rffFormalite: number | undefined;
  rffTotal: number | undefined;
  isActive: boolean;
}
