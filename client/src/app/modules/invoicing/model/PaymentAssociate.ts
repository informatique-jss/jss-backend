import { Affaire } from "../../quotation/model/Affaire";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Invoice } from "../../quotation/model/Invoice";
import { Responsable } from "../../tiers/model/Responsable";
import { Tiers } from "../../tiers/model/Tiers";
import { Payment } from "./Payment";

export interface PaymentAssociate {
  payment: Payment;
  invoices: Invoice[];
  customerOrders: CustomerOrder[];
  affaireRefund: Affaire | undefined;
  tiersRefund: Tiers | undefined;
  responsableOrder: Responsable;
  byPassAmount: number[];
}
