import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { SpecialOffer } from "../../miscellaneous/model/SpecialOffer";
import { Responsable } from "../../tiers/model/Responsable";
import { Tiers } from "../../tiers/model/Tiers";
import { AssoAffaireOrder } from "./AssoAffaireOrder";
import { Confrere } from "./Confrere";

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
  isQuotation: boolean;
  assoAffaireOrders: AssoAffaireOrder[];
}
