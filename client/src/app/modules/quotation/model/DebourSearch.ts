import { CompetentAuthority } from 'src/app/modules/miscellaneous/model/CompetentAuthority';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
export interface DebourSearch {
  competentAuthority: CompetentAuthority;
  minAmount: number;
  maxAmount: number;
  isNonAssociated: boolean;
  isCompetentAuthorityDirectCharge: boolean;
  customerOrder: IndexEntity;
}
