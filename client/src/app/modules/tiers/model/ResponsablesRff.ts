import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Responsable } from "./Responsable";

export interface ResponsablesRff {
  id: number;
  responsableLastName: string | null;
  responsableFirstName: string | null;
  responsables: Responsable[];
  function: string;
  responsableMail: string | null;
  responsablePhone: string | null;
  responsableTurnoverAmountWithTax: number;
  responsableAnnouncementNbr: number;
  responsableFormalityNbr: number;
  responsableTotalCustomerOrders: number;
  responsableSub: boolean;
  responsableGift: number;
  responsableOthers: string;
  responsableCustomerOrders: CustomerOrder[];
}
