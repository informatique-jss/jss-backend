import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";
import { Responsable } from "./Responsable";

export interface TiersRff {
  denomination:  string | null;
  address: string;
  id: number;
  mails: Mail[];
  phones: Phone[];
  rffInsertion: number| null;
  rffTotal: number| null;
  rffFormalite: number| null;
  lastName: string;
  firstName: string;
  responsables: Responsable[];
  turnoverAmountWithoutTax: number;
}
