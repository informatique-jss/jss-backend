import { Announcement } from "./Announcement";
import { Domiciliation } from "./Domiciliation";
import { ProvisionType } from "./ProvisionType";

export interface Provision {
  id: number;
  domiciliation: Domiciliation | undefined;
  announcement: Announcement | undefined;
  provisionType: ProvisionType;
}
