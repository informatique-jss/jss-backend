import { CodePays } from "./referentials/CodePays";
import { TypeVoie } from "./referentials/TypeVoie";

export interface Immeuble {
  roleAdresse: string;
  pays: string;
  codePays: CodePays;
  codePostal: string;
  commune: string;
  codeInseeCommune: string;
  typeVoie: TypeVoie;
  voie: string;
  voieCodifiee: string;
  numVoie: string;
  indiceRepetition: string;
  distributionSpeciale: string;
  communeAncienne: string;
  rgpd: string;
  datePriseEffetAdresse: Date;
  complementLocalisation: string;
  communeDeRattachement: string;
  caracteristiques: string;
  indicateurValidationBAN: boolean;
}

