import { Responsable } from "../../profile/model/Responsable";
import { SpecialOffer } from "../../profile/model/SpecialOffer";
import { AssoAffaireOrder } from "./AssoAffaireOrder";
import { QuotationStatus } from "./QuotationStatus";

export interface Quotation {
  id: number;
  responsable: Responsable | undefined;
  specialOffers: SpecialOffer[] | undefined;
  createdDate: Date;
  description: string;
  assoAffaireOrders: AssoAffaireOrder[];
  quotationStatus: QuotationStatus;
  affairesList: string;
  servicesList: string;
}
