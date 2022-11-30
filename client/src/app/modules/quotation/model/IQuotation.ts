import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { SpecialOffer } from "../../miscellaneous/model/SpecialOffer";
import { Responsable } from "../../tiers/model/Responsable";
import { Tiers } from "../../tiers/model/Tiers";
import { AssoAffaireOrder } from "./AssoAffaireOrder";
import { Confrere } from "./Confrere";
import { QuotationLabelType } from "./QuotationLabelType";
import { RecordType } from "./RecordType";

export interface IQuotation extends IAttachment, IDocument {
  id: number;
  tiers: Tiers | undefined;
  responsable: Responsable | undefined;
  confrere: Confrere | undefined;
  specialOffers: SpecialOffer[] | undefined;
  overrideSpecialOffer: boolean;
  createdDate: Date;
  observations: string;
  description: string;
  quotationLabelType: QuotationLabelType;
  quotationLabel: string;
  customLabelResponsable: Responsable | undefined;
  customLabelTiers: Tiers | undefined;
  recordType: RecordType;
  isQuotation: boolean;
  assoAffaireOrders: AssoAffaireOrder[];
}
