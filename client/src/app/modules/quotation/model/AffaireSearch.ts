import { CompetentAuthority } from '../../miscellaneous/model/CompetentAuthority';
import { IWorkflowElement } from '../../miscellaneous/model/IWorkflowElement';
import { Employee } from "../../profile/model/Employee";
import { Tiers } from '../../tiers/model/Tiers';
import { Affaire } from './Affaire';
import { Status } from './guichet-unique/referentials/Status';

export interface AffaireSearch {
  responsible: Employee | undefined;
  assignedTo: Employee | undefined;
  label: string | undefined;
  status: IWorkflowElement[];
  customerOrders: Tiers[];
  affaire: Affaire;
  waitedCompetentAuthority: CompetentAuthority;
  isMissingQueriesToManualRemind: boolean;
  commercial: Employee | undefined;
  formaliteGuichetUniqueStatus: Status | undefined;
}
