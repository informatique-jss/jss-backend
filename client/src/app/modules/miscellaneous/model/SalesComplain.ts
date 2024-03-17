import { SalesComplainProblem } from './SalesComplainProblem';
import { SalesComplainCause } from './SalesComplainCause';
import { SalesComplainOrigin } from './SalesComplainOrigin';
import { Affaire } from '../../quotation/model/Affaire';

export interface SalesComplain {
	id: number;
  salesProblem: SalesComplainProblem;
  salesCause: SalesComplainCause;
  salesOrigin: SalesComplainOrigin;
	affaire: Affaire;
	complaintDate: Date | undefined;
  idTiers: number | undefined;
  responsableName: string;
  observations: string | undefined;
  customerOrderNumber: string | undefined;
}
