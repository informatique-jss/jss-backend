import { Announcement } from "./Announcement";
import { Domiciliation } from "./Domiciliation";
import { ProvisionFamilyType } from "./ProvisionFamilyType";
import { ProvisionType } from "./ProvisionType";

export interface Provision {
  id: number;
  domiciliation: Domiciliation | undefined;
  announcement: Announcement | undefined;
  provisionType: ProvisionType;
  isEmergency: boolean;
  isRedactedByJss: boolean;
  // Only for front to display the announcements in the good order
  order: number;
  provisionFamilyType: ProvisionFamilyType;
  isDoNotGenerateAnnouncement: boolean;
}
