import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { PaymentType } from "../../miscellaneous/model/PaymentType";
import { BodaccFusion } from "./BodaccFusion";
import { BodaccPublicationType } from "./BodaccPublicationType";
import { BodaccSale } from "./BodaccSale";
import { BodaccSplit } from "./BodaccSplit";
import { BodaccStatus } from './BodaccStatus';
import { TransfertFundsType } from "./TransfertFundsType";

export interface Bodacc extends IAttachment, IDocument {
  id: number;
  bodaccPublicationType: BodaccPublicationType;
  transfertFundsType: TransfertFundsType;
  paymentType: PaymentType;
  bodaccSale: BodaccSale;
  bodaccFusion: BodaccFusion;
  bodaccSplit: BodaccSplit;
  dateOfPublication: Date;
  bodaccStatus: BodaccStatus;
}
