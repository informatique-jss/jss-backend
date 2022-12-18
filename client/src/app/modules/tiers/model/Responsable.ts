import { Civility } from "../../miscellaneous/model/Civility";
import { ITiers } from "./ITiers";
import { SubscriptionPeriodType } from "./SubscriptionPeriodType";
import { Tiers } from "./Tiers";
import { TiersCategory } from "./TiersCategory";
import { TiersType } from "./TiersType";

export interface Responsable extends ITiers {
  tiers: Tiers;
  isActive: boolean;
  isBouclette: boolean;
  function: string;
  building: string;
  floor: string;
  subscriptionPeriodType: SubscriptionPeriodType;
  tiersType: TiersType;
  tiersCategory: TiersCategory;
  firstBilling: Date;
  civility: Civility;
  firstname: string | null;
  lastname: string | null;
  rffFormaliteRate: number;
  rffInsertionRate: number;
  loginWeb: string;
  canViewAllTiersInWeb: boolean;
}
