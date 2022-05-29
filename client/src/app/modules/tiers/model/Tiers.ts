import { DeliveryService } from "../../miscellaneous/model/DeliveryService";
import { PaymentType } from './../../miscellaneous/model/PaymentType';
import { Responsable } from "./Responsable";
import { SpecialOffer } from "./SpecialOffer";
import { ITiers } from './ITiers';
import { TiersCategory } from "./TiersCategory";
import { TiersType } from "./TiersType";

export interface Tiers extends ITiers {
  denomination: string | null;
  isIndividual: boolean;
  deliveryService: DeliveryService;
  intercom: string;
  intercommunityVat: string | null;
  specialOffer: SpecialOffer | null;
  instructions: string;
  paymentType: PaymentType;
  paymentIBAN: string;
  isProvisionalPaymentMandatory: boolean;
  responsables: Responsable[];
}
