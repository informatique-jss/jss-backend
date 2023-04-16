import { CompetentAuthority } from 'src/app/modules/miscellaneous/model/CompetentAuthority';
import { FormaliteStatus } from '../FormaliteStatus';

export interface Formalite {
  id: number;
  observations: string;
  waitedCompetentAuthority: CompetentAuthority;
  formaliteStatus: FormaliteStatus;
}

