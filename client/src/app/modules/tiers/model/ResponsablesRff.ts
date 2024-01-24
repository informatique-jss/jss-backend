import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Responsable } from "./Responsable";

export interface ResponsablesRff {
  id: number;
  lastName: string | null;
  firstName: string | null;
  responsables: Responsable[];
  function: string;
  mails: Mail[];
  phones: Phone[];
  turnoverAmountWithTax: number;
  announcementNbr: number;
  formalityNbr: number;
  totalCustomerOrders: number;
  isSub: boolean;
  gift: boolean;
  others: string;
  customerOrders: CustomerOrder[];
  rffInsertion: number | null;
  rffFormalite: number | null;
  rffTotal: number | null;
}
