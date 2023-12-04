import { IReferential } from "../../administration/model/IReferential";
import { City } from "../../miscellaneous/model/City";
import { Civility } from "../../miscellaneous/model/Civility";
import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { Country } from "../../miscellaneous/model/Country";
import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";
import { FormeExerciceActivitePrincipal } from "./guichet-unique/referentials/FormeExerciceActivitePrincipal";
import { FormeJuridique } from "./guichet-unique/referentials/FormeJuridique";

export interface Affaire extends IReferential {
  id: number;
  civility: Civility;
  firstname: string;
  lastname: string;
  denomination: string;
  isIndividual: boolean;
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
  externalReference: string;
  observations: string;
  shareCapital: number;
  paymentIban: string;
  paymentBic: string;
  isUnregistered: boolean;
  intercommunityVat: string | null;
  legalForm: FormeJuridique;
  mainActivity: FormeExerciceActivitePrincipal;
  competentAuthority: CompetentAuthority;
}
