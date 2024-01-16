import { AbandonReason } from "../../miscellaneous/model/AbandonReason";
import { CustomerOrderOrigin } from "../../miscellaneous/model/CustomerOrderOrigin";
import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { SpecialOffer } from "../../miscellaneous/model/SpecialOffer";
import { Employee } from '../../profile/model/Employee';
import { Responsable } from "../../tiers/model/Responsable";
import { Tiers } from "../../tiers/model/Tiers";
import { AssoAffaireOrder } from "./AssoAffaireOrder";
import { Confrere } from "./Confrere";

export interface IQuotation extends IAttachment, IDocument {
  id: number;
  assignedTo: Employee;
  tiers: Tiers | undefined;
  responsable: Responsable | undefined;
  confrere: Confrere | undefined;
  specialOffers: SpecialOffer[] | undefined;
  createdDate: Date;
  observations: string;
  description: string;
  instructions: string;
  isQuotation: boolean;
  assoAffaireOrders: AssoAffaireOrder[];
  firstReminderDateTime: Date;
  secondReminderDateTime: Date;
  thirdReminderDateTime: Date;
  customerMailCustomMessage: string;
  customerOrderOrigin: CustomerOrderOrigin;
  abandonReason: AbandonReason;
}
