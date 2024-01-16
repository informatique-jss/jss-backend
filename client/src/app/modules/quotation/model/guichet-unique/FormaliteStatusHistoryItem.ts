import { Partenaire } from "./Partenaire";
import { PartnerCenter } from "./PartnerCenter";
import { Status } from "./referentials/Status";

export interface FormaliteStatusHistoryItem {
  id: number;
  sourceStatus: Status;
  status: Status;
  created: Date;
  partner: Partenaire;
  partnerCenter: PartnerCenter;
}
