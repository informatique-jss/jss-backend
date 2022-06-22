import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { Department } from "../../miscellaneous/model/Department";
import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { PaymentType } from "../../miscellaneous/model/PaymentType";
import { BodaccFusion } from "./BodaccFusion";
import { BodaccPublicationType } from "./BodaccPublicationType";
import { BodaccSale } from "./BodaccSale";
import { BodaccSplit } from "./BodaccSplit";
import { TransfertFundsType } from "./TransfertFundsType";

export interface Bodacc extends IAttachment {
  id: number;
  bodaccPublicationType: BodaccPublicationType;
  transfertFundsType: TransfertFundsType;
  isDoublePublication: boolean;
  department: Department;
  competentAuthority: CompetentAuthority;
  paymentType: PaymentType;
  bodaccSale: BodaccSale;
  bodaccFusion: BodaccFusion;
  bodaccSplit: BodaccSplit;
}
