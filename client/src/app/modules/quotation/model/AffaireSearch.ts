import { Employee } from "../../profile/model/Employee";
import { AffaireStatus } from "./AffaireStatus";

export interface AffaireSearch {
  responsible: Employee;
  assignedTo: Employee;
  affaireStatus: AffaireStatus[];
  label: string;
}
