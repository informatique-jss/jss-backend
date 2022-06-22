import { City } from "./City";
import { CompetentAuthorityType } from "./CompetentAuthorityType";
import { Department } from "./Department";
import { Mail } from "./Mail";
import { PaymentType } from "./PaymentType";
import { Phone } from "./Phone";

export interface CompetentAuthority {
  id: number;
  code: string;
  label: string;
  competentAuthorityType: CompetentAuthorityType;
  departments: Department[];
  paymentTypes: PaymentType[];
  phones: Phone[];
  mails: Mail[];
  iban: string;
  city: City;
}
