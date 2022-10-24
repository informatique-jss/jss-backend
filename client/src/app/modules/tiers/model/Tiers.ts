import { Civility } from "../../miscellaneous/model/Civility";
import { DeliveryService } from "../../miscellaneous/model/DeliveryService";
import { SpecialOffer } from "../../miscellaneous/model/SpecialOffer";
import { PaymentType } from './../../miscellaneous/model/PaymentType';
import { Competitor } from "./Competitor";
import { ITiers } from './ITiers';
import { Responsable } from "./Responsable";
import { TiersCategory } from "./TiersCategory";
import { TiersType } from "./TiersType";

export interface Tiers extends ITiers {
  denomination: string | null;
  isIndividual: boolean;
  deliveryService: DeliveryService;
  intercom: string;
  intercommunityVat: string | null;
  specialOffers: SpecialOffer[];
  instructions: string;
  paymentType: PaymentType;
  paymentIban: string;
  isProvisionalPaymentMandatory: boolean;
  isSepaMandateReceived: boolean;
  responsables: Responsable[];
  competitors: Competitor[];
  tiersType: TiersType;
  tiersCategory: TiersCategory;
  firstBilling: Date;
  civility: Civility;
  firstname: string | null;
  lastname: string | null;
  rffFormaliteRate: number;
  rffInsertionRate: number;
  responsibleSuscribersNumber: number;
  webAccountNumber: number;
}
