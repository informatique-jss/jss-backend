import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { Department } from "../../miscellaneous/model/Department";
import { PaymentType } from "../../miscellaneous/model/PaymentType";
import { BodaccPublicationType } from "./BodaccPublicationType";
import { TransfertFundsType } from "./TransfertFundsType";

export interface Bodacc {
  id: number;
  bodaccPublicationType: BodaccPublicationType;
  transfertFundsType: TransfertFundsType;
  isDoublePublication: boolean;
  department: Department;
  competentAuthority: CompetentAuthority;
  paymentType: PaymentType;
}
