import { CompetentAuthority } from '../../miscellaneous/model/CompetentAuthority';
import { SimpleProvisionStatus } from './SimpleProvisonStatus';
export interface SimpleProvision {
  id: number;
  simpleProvisionStatus: SimpleProvisionStatus;
  observations: string;
  waitedCompetentAuthority: CompetentAuthority;
}

