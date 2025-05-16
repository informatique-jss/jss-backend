import { CompetentAuthority } from 'src/app/modules/miscellaneous/model/CompetentAuthority';
import { FormaliteStatus } from './FormaliteStatus';
import { FormaliteGuichetUnique } from './guichet-unique/FormaliteGuichetUnique';
import { FormaliteInfogreffe } from './infogreffe/FormaliteInfogreffe';

export interface Formalite {
  id: number;
  waitedCompetentAuthority: CompetentAuthority;
  formaliteStatus: FormaliteStatus;
  formalitesGuichetUnique: FormaliteGuichetUnique[];
  formalitesInfogreffe: FormaliteInfogreffe[];
}

