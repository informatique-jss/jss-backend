import { Formalite } from "../Formalite";
import { FormaliteStatusHistoryItem } from "./FormaliteStatusHistoryItem";
import { ValidationRequest } from "./ValidationRequest";
import { Status } from "./referentials/Status";

export interface FormaliteGuichetUnique {
  id: number;
  liasseNumber: string;
  referenceMandataire: string;
  formalite: Formalite;
  created: string;
  isFormality: boolean;
  isAnnualAccounts: boolean;
  isActeDeposit: boolean;
  validationsRequests: ValidationRequest[];
  formaliteStatusHistoryItems: FormaliteStatusHistoryItem[];
  status: Status;
  isAuthorizedToSign: boolean;
}
