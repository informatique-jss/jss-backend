import { Attachment } from "../../miscellaneous/model/Attachment";
import { AssoAffaireOrder } from "./AssoAffaireOrder";
import { CustomerOrder } from "./CustomerOrder";

export interface AttachmentMailRequest {
  customerOrder: CustomerOrder;
  assoAffaireOrder: AssoAffaireOrder;
  attachements: Attachment[];
  sendToMe: boolean;
  comment: string;
}
