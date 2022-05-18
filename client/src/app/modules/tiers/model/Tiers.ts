import { Employee } from "../../profile/model/Employee";
import { Civility } from "../../miscellaneous/model/Civility";
import { TiersCategory } from "./TiersCategory";
import { TiersType } from "./TiersType";
import { Language } from "../../miscellaneous/model/Language";
import { DeliveryService } from "../../miscellaneous/model/DeliveryService";

export interface Tiers {
  id: number;
  tiersType: TiersType;
  denomination: string | null;
  firstBilling: Date;
  isIndividual: boolean;
  civility: Civility | null;
  firstname: string | null;
  lastname: string | null;
  tiersCategory: TiersCategory;
  salesEmployee: Employee;
  formalisteEmployee: Employee;
  insertionEmployee: Employee;
  mailRecipient: string | null;
  language: Language;
  deliveryService: DeliveryService;
}
