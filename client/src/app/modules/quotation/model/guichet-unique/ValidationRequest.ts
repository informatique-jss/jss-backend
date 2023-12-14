import { Partenaire } from "./Partenaire";
import { PartnerCenter } from "./PartnerCenter";
import { RejectionReason } from "./referentials/RejectionReason";
import { ValidationsRequestStatus } from "./referentials/ValidationsRequestStatus";

export interface ValidationRequest {
  id: number;
  validationNumber: number;
  status: ValidationsRequestStatus;
  rejectionReasons: RejectionReason[];
  partner: Partenaire;
  partnerCenter: PartnerCenter;
  closestRegularizationRequestExpirationDate: Date;
  lastInternalObservation: string;
  statusDate: Date;
  created: Date;
  updated: Date;
  validationObservation: string;
}
