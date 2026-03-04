import { RejectionCause } from "../RejectionCause";
import { RegularizationObject } from "./RegularizationObject";

export interface RegularizationRequest {
  id: number;
  status: string;
  regularizationObjects: RegularizationObject[];
  deadline: string;
  isActivityRegularization: boolean;
  mandatoryPartner: string;
  forbiddenPartner: string;
  isRegularizedFromIhm: boolean;
  created: string;
  updated: string;
  rejectionCause: RejectionCause;
}
