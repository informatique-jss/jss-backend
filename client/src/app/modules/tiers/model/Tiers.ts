import { DeliveryService } from "../../miscellaneous/model/DeliveryService";
import { SpecialOffer } from "../../miscellaneous/model/SpecialOffer";
import { PaymentType } from './../../miscellaneous/model/PaymentType';
import { ITiers } from './ITiers';
import { Responsable } from "./Responsable";

export interface Tiers extends ITiers {
  denomination: string | null;
  isIndividual: boolean;
  deliveryService: DeliveryService;
  intercom: string;
  intercommunityVat: string | null;
  specialOffers: SpecialOffer[];
  instructions: string;
  paymentType: PaymentType;
  paymentIBAN: string;
  isProvisionalPaymentMandatory: boolean;
  isSepaMandateReceived: boolean;
  responsables: Responsable[];
}
