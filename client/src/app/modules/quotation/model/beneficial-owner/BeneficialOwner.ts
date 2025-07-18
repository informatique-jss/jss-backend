import { City } from "src/app/modules/miscellaneous/model/City";
import { Country } from "src/app/modules/miscellaneous/model/Country";
import { Affaire } from "../Affaire";
import { OtherControls } from "./OtherControls";
import { ShareHolding } from "./ShareHolding";
import { VotingRights } from "./VotingRights";


export interface BeneficialOwner {
  id: number;
  birthName: string;
  usedName: string;
  firstNames: string;
  nationality: string;
  birthDate: Date;
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

