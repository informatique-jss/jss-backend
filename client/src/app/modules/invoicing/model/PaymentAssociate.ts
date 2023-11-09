import { Affaire } from "../../quotation/model/Affaire";
import { Confrere } from "../../quotation/model/Confrere";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Invoice } from "../../quotation/model/Invoice";
import { Tiers } from "../../tiers/model/Tiers";
import { Payment } from "./Payment";

export interface PaymentAssociate {
  payment: Payment;
  invoices: Invoice[];
  customerOrders: CustomerOrder[];
  affaireRefund: Affaire | undefined;
  tiersRefund: Tiers | undefined;
  confrereRefund: Confrere | undefined;
  tiersOrder: Tiers;
  byPassAmount: number[];
}
