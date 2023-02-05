import { CompetentAuthority } from '../../miscellaneous/model/CompetentAuthority';
import { IAttachment } from '../../miscellaneous/model/IAttachment';
import { SimpleProvisionStatus } from './SimpleProvisonStatus';
export interface SimpleProvision extends IAttachment {
  id: number;
  simpleProvisionStatus: SimpleProvisionStatus;
  observations: string;
  waitedCompetentAuthority: CompetentAuthority;
}

