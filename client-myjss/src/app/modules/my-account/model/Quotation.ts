import { SpecialOffer } from "../../profile/model/SpecialOffer";
import { IQuotation } from "../../quotation/model/IQuotation";
import { QuotationStatus } from "./QuotationStatus";

export interface Quotation extends IQuotation {
  specialOffers: SpecialOffer[] | undefined;
  createdDate: Date;
  description: string;
  quotationStatus: QuotationStatus;
  affairesList: string;
  servicesList: string;
}
