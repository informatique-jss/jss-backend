import { Affaire } from "./Affaire";
import { CustomerOrder } from "./CustomerOrder";
import { Quotation } from "./Quotation";
import { Service } from "./Service";

export interface AssoAffaireOrder {
  id: number;
  affaire: Affaire;
  services: Service[];
  customerOrder: CustomerOrder;
  quotation: Quotation;
}
