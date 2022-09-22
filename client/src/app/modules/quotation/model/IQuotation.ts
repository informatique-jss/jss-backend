import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";
import { SpecialOffer } from "../../miscellaneous/model/SpecialOffer";
import { Responsable } from "../../tiers/model/Responsable";
import { Tiers } from "../../tiers/model/Tiers";
import { Confrere } from "./Confrere";
import { Provision } from "./Provision";
import { QuotationLabelType } from "./QuotationLabelType";
import { QuotationStatus } from "./QuotationStatus";
import { RecordType } from "./RecordType";

export interface IQuotation extends IAttachment, IDocument {
  id: number;
  tiers: Tiers | undefined;
  responsable: Responsable | undefined;
  confrere: Confrere | undefined;
  specialOffers: SpecialOffer[] | undefined;
  overrideSpecialOffer: boolean;
  createdDate: Date;
  quotationStatus: QuotationStatus;
  observations: string;
  description: string;
  quotationLabelType: QuotationLabelType;
  quotationLabel: string;
  customLabelResponsable: Responsable | undefined;
  customLabelTiers: Tiers | undefined;
  recordType: RecordType;
  provisions: Provision[];
  mails: Mail[];
  phones: Phone[];
  isQuotation: boolean;
}
