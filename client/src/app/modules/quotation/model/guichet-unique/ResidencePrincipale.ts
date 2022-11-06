import { AdresseDomicile } from "./AdresseDomicile";
import { Publication } from "./Publication";

export interface ResidencePrincipale {
  residenceInsaisissable: boolean;
  nomResidence: string;
  dateEffet: Date;
  adresse: AdresseDomicile;
  publication: Publication;
  is28PTriggered: boolean;
}

