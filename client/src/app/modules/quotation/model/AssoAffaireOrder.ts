import { Employee } from "../../profile/model/Employee";
import { Affaire } from "./Affaire";
import { AffaireStatus } from "./AffaireStatus";
import { CustomerOrder } from "./CustomerOrder";
import { Provision } from "./Provision";
import { Quotation } from "./Quotation";

export interface AssoAffaireOrder {
  id: number;
  affaire: Affaire;
  assignedTo: Employee;
  provisions: Provision[];
  affaireStatus: AffaireStatus;
  customerOrder: CustomerOrder;
  quotation: Quotation;
}
