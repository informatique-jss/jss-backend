import { City } from "../../miscellaneous/model/City";
import { Civility } from "../../miscellaneous/model/Civility";
import { Country } from "../../miscellaneous/model/Country";
import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { Language } from "../../miscellaneous/model/Language";
import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";
import { Document } from "../../miscellaneous/model/Document";
import { Employee } from "../../profile/model/Employee";
import { TiersCategory } from "./TiersCategory";
import { TiersFollowup } from './TiersFollowup';
import { TiersType } from "./TiersType";
import { IDocument } from "../../miscellaneous/model/IDocument";

export interface ITiers extends IAttachment, IDocument {
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
  tiersFollowups: TiersFollowup[];
  rcaFormaliteRate: number;
  rcaInsertionRate: number;
}
