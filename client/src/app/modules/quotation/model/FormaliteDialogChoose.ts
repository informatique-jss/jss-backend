import { CompetentAuthority } from 'src/app/modules/miscellaneous/model/CompetentAuthority';
import { ActeDeposit } from './ActeDeposit';
import { FormaliteStatus } from './FormaliteStatus';
import { FormaliteGuichetUnique } from './guichet-unique/FormaliteGuichetUnique';
import { FormaliteInfogreffe } from './infogreffe/FormaliteInfogreffe';

export interface FormaliteDialogChoose {
  competentAuthorityServiceProvider: CompetentAuthority;
  formaliteGuichetUnique: FormaliteGuichetUnique;
  formaliteInfogreffe: FormaliteInfogreffe;
}
