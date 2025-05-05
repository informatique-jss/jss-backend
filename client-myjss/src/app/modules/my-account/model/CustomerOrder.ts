import { SpecialOffer } from "../../profile/model/SpecialOffer";
import { IQuotation } from "../../quotation/model/IQuotation";
import { CustomerOrderStatus } from "./CustomerOrderStatus";

export interface CustomerOrder extends IQuotation {
  specialOffers: SpecialOffer[] | undefined;
  createdDate: Date;
  description: string;
  customerOrderStatus: CustomerOrderStatus;
  isGifted: boolean;
  isRecurring: boolean;
  recurringStartDate: Date;
  recurringEndDate: Date;
  isPayed: boolean;
  affairesList: string;
  servicesList: string;
  hasMissingInformations: boolean;
}
