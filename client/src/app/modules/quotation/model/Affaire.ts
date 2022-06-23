import { City } from "../../miscellaneous/model/City";
import { Civility } from "../../miscellaneous/model/Civility";
import { Country } from "../../miscellaneous/model/Country";
import { LegalForm } from "../../miscellaneous/model/LegalForm";
import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";
import { CustomerOrder } from "./CustomerOrder";
import { Provision } from "./Provision";
import { Quotation } from "./Quotation";

export interface Affaire {
  id: number;
  provisions: Provision[];
  quotation: Quotation;
  customerOrder: CustomerOrder;
  civility: Civility;
  firstname: string;
  lastname: string;
  denomination: string;
  isIndividual: boolean;
  legalForm: LegalForm | undefined;
  siren: string;
  siret: string;
  rna: string;
  city: City;
  country: Country;
  postalCode: string;
  address: string;
  mails: Mail[];
  phones: Phone[];
  externalReference: string;
  observations: string;
  shareCapital: number;
}
