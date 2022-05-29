import { City } from "../../miscellaneous/model/City";
import { Civility } from "../../miscellaneous/model/Civility";
import { Country } from "../../miscellaneous/model/Country";
import { Language } from "../../miscellaneous/model/Language";
import { Employee } from "../../profile/model/Employee";
import { Mail } from "./Mail";
import { Phone } from "./Phone";
import { TiersAttachment } from './TiersAttachment';
import { TiersCategory } from "./TiersCategory";
import { TiersDocument } from "./TiersDocument";
import { TiersFollowup } from './TiersFollowup';
import { TiersType } from "./TiersType";

export interface ITiers {
  id: number;
  tiersType: TiersType;
  tiersCategory: TiersCategory;
  firstBilling: Date;
  civility: Civility | null;
  firstname: string | null;
  lastname: string | null;
  salesEmployee: Employee | null;
  formalisteEmployee: Employee | null;
  insertionEmployee: Employee | null;
  mailRecipient: string | null;
  language: Language;
  address: string;
  postalCode: string;
  city: City;
  country: Country;
  mails: Mail[];
  phones: Phone[];
  observations: string;
  documents: TiersDocument[];
  tiersAttachments: TiersAttachment[];
  tiersFollowups: TiersFollowup[];
  rcaFormaliteRate: number;
  rcaInsertionRate: number;
}
