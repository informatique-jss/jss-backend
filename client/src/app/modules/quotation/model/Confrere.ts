import { Department } from "../../miscellaneous/model/Department";
import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";
import { WeekDay } from "../../miscellaneous/model/WeekDay";
import { JournalType } from "./JournalType";

export interface Confrere {
  id: number;
  denomination: string;
  departments: Department[];
  mails: Mail[];
  phones: Phone[];
  discountRate: number;
  reinvoicing: number;
  weekDays: WeekDay[];
  journalType: JournalType[];
  lastShipmentForPublication: string;
  preference: string;
  shippingCosts: number;
  administrativeFees: number;
  numberOfPrint: number;
}
