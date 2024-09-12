import { CustomerOrderOrigin } from "../../miscellaneous/model/CustomerOrderOrigin";
import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { QuotationAbandonReason } from "../../miscellaneous/model/QuotationAbandonReason";
import { SpecialOffer } from "../../miscellaneous/model/SpecialOffer";
import { Employee } from '../../profile/model/Employee';
import { Responsable } from "../../tiers/model/Responsable";
import { Tiers } from "../../tiers/model/Tiers";
import { AssoAffaireOrder } from "./AssoAffaireOrder";
import { Confrere } from "./Confrere";
import { CustomerOrderComment } from "./CustomerOrderComment";

export interface IQuotation extends IAttachment, IDocument {
  id: number;
  assignedTo: Employee;
  tiers: Tiers | undefined;
  responsable: Responsable | undefined;
  confrere: Confrere | undefined;
  specialOffers: SpecialOffer[] | undefined;
  createdDate: Date;
  isQuotation: boolean;
  assoAffaireOrders: AssoAffaireOrder[];
  firstReminderDateTime: Date;
  secondReminderDateTime: Date;
  thirdReminderDateTime: Date;
  customerOrderOrigin: CustomerOrderOrigin;
  abandonReason: QuotationAbandonReason;
  customerOrderComments: CustomerOrderComment[];
}
