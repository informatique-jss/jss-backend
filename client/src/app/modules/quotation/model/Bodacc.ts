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
  paymentType: PaymentType;
  bodaccSale: BodaccSale;
  bodaccFusion: BodaccFusion;
  bodaccSplit: BodaccSplit;
  dateOfPublication: Date;
}
