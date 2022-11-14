import { Employee } from "../../profile/model/Employee";
import { Affaire } from "./Affaire";
import { CustomerOrder } from "./CustomerOrder";
import { Provision } from "./Provision";

export interface AssoAffaireOrderFlatten {
  id: number;
  affaire: Affaire;
  assignedTo: Employee;
  provision: Provision;
  customerOrder: CustomerOrder;
}
