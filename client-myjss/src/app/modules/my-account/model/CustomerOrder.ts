import { Responsable } from "../../profile/model/Responsable";
import { SpecialOffer } from "../../profile/model/SpecialOffer";
import { AssoAffaireOrder } from "./AssoAffaireOrder";
import { CustomerOrderStatus } from "./CustomerOrderStatus";

export interface CustomerOrder {
  id: number;
  responsable: Responsable | undefined;
  assoAffaireOrders: AssoAffaireOrder[];
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
