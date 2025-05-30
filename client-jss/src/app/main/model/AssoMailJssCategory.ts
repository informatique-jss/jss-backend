import { JssCategory } from "./JssCategory";
import { Mail } from "./Mail";

export interface AssoMailJssCategory {
  id: number;
  mail: Mail;
  jssCategory: JssCategory;
  lastConsultationDate: Date;
}
