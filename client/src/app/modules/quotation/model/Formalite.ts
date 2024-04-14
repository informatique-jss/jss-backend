import { CompetentAuthority } from 'src/app/modules/miscellaneous/model/CompetentAuthority';
import { ActeDeposit } from './ActeDeposit';
import { FormaliteStatus } from './FormaliteStatus';
import { FormaliteGuichetUnique } from './guichet-unique/FormaliteGuichetUnique';

export interface Formalite {
  id: number;
  waitedCompetentAuthority: CompetentAuthority;
  formaliteStatus: FormaliteStatus;
  competentAuthorityServiceProvider: CompetentAuthority;
  formalitesGuichetUnique: FormaliteGuichetUnique[];
  acteDeposit: ActeDeposit;
}

