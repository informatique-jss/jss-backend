import { Affaire } from "./Affaire";
import { CustomerOrder } from "./CustomerOrder";
import { Quotation } from "./Quotation";

export interface Provision {
  id: number;
  affaire: Affaire;
  quotation: Quotation;
  customerOrder: CustomerOrder;
}
