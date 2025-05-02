import { Announcement } from "./Announcement";
import { Domiciliation } from "./Domiciliation";
import { ProvisionType } from "./ProvisionType";

export interface Provision {
  id: number;
  domiciliation: Domiciliation | undefined;
  announcement: Announcement | undefined;
  provisionType: ProvisionType;
  isEmergency: boolean;
  // Only for front to display the announcements in the good order
  order: number;
}
