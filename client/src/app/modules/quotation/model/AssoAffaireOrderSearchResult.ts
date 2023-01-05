import { Employee } from "../../profile/model/Employee";

export interface AssoAffaireOrderSearchResult {
  affaireLabel: string;
  affaireAddress: string;
  tiersLabel: string;
  responsableLabel: string;
  confrereLabel: string;
  responsibleId: number;
  assignedToId: number;
  provisionTypeLabel: string;
  statusLabel: Employee;
  assoId: number;
  provisionId: number;
  isEmergency: boolean;
}
