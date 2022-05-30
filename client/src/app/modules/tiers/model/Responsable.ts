import { ITiers } from "./ITiers";
import { JssSubscription } from "./JssSubscription";
import { SubscriptionPeriodType } from "./SubscriptionPeriodType";
import { Tiers } from "./Tiers";

export interface Responsable extends ITiers {
  tiers: Tiers;
  isActive: boolean;
  isBouclette: boolean;
  function: string;
  building: string;
  floor: string;
  jssSubscription: JssSubscription;
  subscriptionPeriodType: SubscriptionPeriodType;
  mailsCreationAffaire: Responsable[];
  mailsProvisionningConfirmation: Responsable[];
  mailsMissingItemFormality: Responsable[];
}
