import { Responsable } from "../../profile/model/Responsable";
import { SpecialOffer } from "../../profile/model/SpecialOffer";
import { IQuotation } from "../../quotation/model/IQuotation";
import { QuotationStatus } from "./QuotationStatus";

export interface Quotation extends IQuotation {
  responsable: Responsable | undefined;
  specialOffers: SpecialOffer[] | undefined;
  createdDate: Date;
  description: string;
  quotationStatus: QuotationStatus;
  affairesList: string;
  servicesList: string;
}
