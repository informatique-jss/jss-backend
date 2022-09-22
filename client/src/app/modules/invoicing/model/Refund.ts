import { Confrere } from "../../quotation/model/Confrere";
import { Tiers } from "../../tiers/model/Tiers";

export interface Refund {
  id: number;
  refundAmount: number;
  label: string;
  refundDateTime: Date;
  tiers: Tiers;
  confrere: Confrere;
}
