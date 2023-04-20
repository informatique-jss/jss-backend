import { CompetentAuthority } from 'src/app/modules/miscellaneous/model/CompetentAuthority';
import { FormaliteStatus } from './FormaliteStatus';
import { FormaliteGuichetUnique } from './guichet-unique/FormaliteGuichetUnique';

export interface Formalite {
  id: number;
  observations: string;
  waitedCompetentAuthority: CompetentAuthority;
  formaliteStatus: FormaliteStatus;
  competentAuthorityServiceProvider: CompetentAuthority;
  formaliteGuichetUnique: FormaliteGuichetUnique;
}

