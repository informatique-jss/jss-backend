import { TypeLocataireGerantMandataire } from "./referentials/TypeLocataireGerantMandataire";

export interface LocationGeranceMandat {
  destinationLocationGeranceMandat: string;
  typeLocataireGerantMandataire: TypeLocataireGerantMandataire;
  dateEffet: Date;
}

