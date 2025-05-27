import { Mail } from "../../general/model/Mail";
import { City } from "../../profile/model/City";
import { Civility } from "../../profile/model/Civility";
import { Country } from "../../profile/model/Country";
import { Phone } from "../../profile/model/Phone";
import { CompetentAuthority } from "./CompetentAuthority";
import { FormeExerciceActivitePrincipal } from "./FormeExerciceActivitePrincipal";
import { FormeJuridique } from "./FormeJuridique";

export interface Affaire {
  id: number;
  civility: Civility;
  firstname: string;
  lastname: string;
  denomination: string;
  siren: string;
  siret: string;
  rna: string;
  city: City;
  country: Country;
  postalCode: string;
  cedexComplement: string;
  address: string;
  mails: Mail[];
  phones: Phone[];
  legalForm: FormeJuridique;
  mainActivity: FormeExerciceActivitePrincipal;
  competentAuthority: CompetentAuthority;
  acronym: string;
  employeeNumber: number;
  apeCodes: string;
  paymentIban: string;
  paymentBic: string;
  isIndividual: boolean;
}
