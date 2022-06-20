import { Gift } from "../../miscellaneous/model/Gift";
import { Employee } from "../../profile/model/Employee";
import { Responsable } from "./Responsable";
import { Tiers } from "./Tiers";
import { TiersFollowupType } from "./TiersFollowupType";

export interface TiersFollowup {
  id: number;
  tiers: Tiers;
  responsable: Responsable;
  tiersFollowupType: TiersFollowupType;
  gift: Gift;
  salesEmployee: Employee | undefined;
  followupDate: Date;
  observations: string;
}
