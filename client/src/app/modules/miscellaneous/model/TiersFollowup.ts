import { Gift } from "../../miscellaneous/model/Gift";
import { Employee } from "../../profile/model/Employee";
import { Confrere } from "../../quotation/model/Confrere";
import { Invoice } from "../../quotation/model/Invoice";
import { Responsable } from "../../tiers/model/Responsable";
import { Tiers } from "../../tiers/model/Tiers";
import { TiersFollowupType } from "./TiersFollowupType";

export interface TiersFollowup {
  id: number;
  tiers: Tiers;
  responsable: Responsable;
  confrere: Confrere;
  invoice: Invoice;
  tiersFollowupType: TiersFollowupType;
  gift: Gift;
  salesEmployee: Employee | undefined;
  followupDate: Date;
  observations: string;
}
