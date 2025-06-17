import { CompetentAuthority } from '../../miscellaneous/model/CompetentAuthority';
import { ActeDeposit } from './ActeDeposit';
import { FormaliteStatus } from './FormaliteStatus';
import { FormaliteGuichetUnique } from './guichet-unique/FormaliteGuichetUnique';
import { FormaliteInfogreffe } from './infogreffe/FormaliteInfogreffe';

export interface Formalite {
  id: number;
  waitedCompetentAuthority: CompetentAuthority;
  formaliteStatus: FormaliteStatus;
  formalitesGuichetUnique: FormaliteGuichetUnique[];
  formalitesInfogreffe: FormaliteInfogreffe[];
  acteDeposit: ActeDeposit;
}

