import { Affaire } from "../../my-account/model/Affaire";
import { City } from "../../profile/model/City";
import { Country } from "../../profile/model/Country";
import { OtherControls } from "./OtherControls";
import { ShareHolding } from "./ShareHolding";
import { VotingRights } from "./VotingRights";


export interface BeneficialOwner {
  id: number;
  birthName: string;
  usedName: string;
  firstNames: string;
  pseudonym: string;
  nationality: string;
  birthDate: Date;
  birthDepartement: number;
  birthCity: string;
  birthCountry: Country;
  residenceAddress: string;
  postalCode: string;
  city: City;
  country: Country;
  creationDate: Date;
  shareHolding: ShareHolding;
  votingRights: VotingRights;
  otherControls: OtherControls;
  affaire: Affaire;
}

