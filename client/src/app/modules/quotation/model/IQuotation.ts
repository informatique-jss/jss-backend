import { Voucher } from "../../crm/model/Voucher";
import { CustomerOrderOrigin } from "../../miscellaneous/model/CustomerOrderOrigin";
import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { QuotationAbandonReason } from "../../miscellaneous/model/QuotationAbandonReason";
import { SpecialOffer } from "../../miscellaneous/model/SpecialOffer";
import { Responsable } from "../../tiers/model/Responsable";
import { AssoAffaireOrder } from "./AssoAffaireOrder";
import { CustomerOrderComment } from "./CustomerOrderComment";

export interface IQuotation extends IAttachment, IDocument {
  id: number;
  validationId: number;
  responsable: Responsable | undefined;
  specialOffers: SpecialOffer[] | undefined;
  createdDate: Date;
  description: string;
  isQuotation: boolean;
  assoAffaireOrders: AssoAffaireOrder[];
  firstReminderDateTime: Date;
  secondReminderDateTime: Date;
  thirdReminderDateTime: Date;
  customerOrderOrigin: CustomerOrderOrigin;
  abandonReason: QuotationAbandonReason;
  customerOrderComments: CustomerOrderComment[];
  lastStatusUpdate: Date;
  affairesList: string;
  servicesList: string;
  isHasNotifications: boolean;
  voucher: Voucher;
}
