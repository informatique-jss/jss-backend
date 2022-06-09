import { JournalType } from "./JournalType";

export interface ConfrereFlatten {
  id: number;
  denomination: string;
  departments: string;
  mails: string;
  phones: string;
  discountRate: number;
  reinvoicing: number;
  weekDays: string;
  journalType: JournalType;
  lastShipmentForPublication: string;
  preference: string;
  shippingCosts: number;
  administrativeFees: number;
  numberOfPrint: number;
}
