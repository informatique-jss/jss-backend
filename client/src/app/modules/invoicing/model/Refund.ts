import { Affaire } from "../../quotation/model/Affaire";
import { Confrere } from "../../quotation/model/Confrere";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Invoice } from "../../quotation/model/Invoice";
import { RefundType } from "../../tiers/model/RefundType";
import { Tiers } from "../../tiers/model/Tiers";

export interface Refund {
  id: number;
  refundAmount: number;
  label: string;
  refundDateTime: Date;
  tiers: Tiers;
  confrere: Confrere;
  affaire: Affaire;
  invoice: Invoice;
  customerOrder: CustomerOrder;
  refundIBAN: string;
  refundBic: string;
  isMatched: boolean;
  isAlreadyExported: boolean;
  refundType: RefundType;
}
