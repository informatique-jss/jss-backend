import { City } from "./City";
import { Civility } from "./Civility";
import { Country } from "./Country";
import { Employee } from "./Employee";
import { Mail } from "./Mail";
import { Phone } from "./Phone";
import { Tiers } from "./Tiers";

export interface Responsable {
  id: number;
  civility: Civility;
  firstname: string | null;
  lastname: string | null;
  salesEmployee: Employee | undefined;
  mail: Mail;
  phones: Phone[];
  tiers: Tiers;
  address: string;
  addressComplement: string;
  postalCode: string;
  cedexComplement: string;
  city: City;
  country: Country;
  documents: Document[];
  numberOfPostsSharingAuthorized: number;
}
