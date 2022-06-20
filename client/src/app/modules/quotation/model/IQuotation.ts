import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { SpecialOffer } from "../../miscellaneous/model/SpecialOffer";
import { Responsable } from "../../tiers/model/Responsable";
import { Tiers } from "../../tiers/model/Tiers";
import { Provision } from "./Provision";
import { QuotationLabelType } from "./QuotationLabelType";
import { QuotationStatus } from "./QuotationStatus";
import { RecordType } from "./RecordType";

export interface IQuotation extends IAttachment, IDocument {
  id: number;
  tiers: Tiers | null;
  responsable: Responsable | null;
  specialOffers: SpecialOffer[] | undefined;
  createdDate: Date;
  quotationStatus: QuotationStatus;
  observations: string;
  description: string;
  quotationLabelType: QuotationLabelType;
  quotationLabel: string;
  recordType: RecordType;
  provisions: Provision[];
}
