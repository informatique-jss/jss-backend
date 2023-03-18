import { Gift } from "../../miscellaneous/model/Gift";
import { Employee } from "../../profile/model/Employee";
import { TiersFollowupType } from "./TiersFollowupType";

export interface TiersFollowup {
  id: number;
  tiersFollowupType: TiersFollowupType;
  gift: Gift;
  giftNumber: number;
  salesEmployee: Employee | undefined;
  followupDate: Date;
  observations: string;
}
