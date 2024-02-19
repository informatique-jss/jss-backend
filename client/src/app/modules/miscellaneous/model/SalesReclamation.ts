import { SalesReclamationProblem } from './SalesReclamationProblem';
import { SalesReclamationCause } from './SalesReclamationCause';
import { SalesReclamationOrigin } from './SalesReclamationOrigin';
import { Affaire } from '../../quotation/model/Affaire';

export interface SalesReclamation {
	id: number;
  salesProblem: SalesReclamationProblem;
  salesCause: SalesReclamationCause;
  salesOrigin: SalesReclamationOrigin;
	affaire: Affaire;
	complaintDate: Date | undefined;
  idTiers: number | undefined;
  responsableName: string;
  observations: string | undefined;
}
