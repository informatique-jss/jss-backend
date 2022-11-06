import { Caracteristiques } from "./Caracteristiques";
import { CodePays } from "./referentials/CodePays";
import { TypeVoie } from "./referentials/TypeVoie";

export interface AdresseDomicile {
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
  caracteristiques: Caracteristiques;
  indicateurValidationBAN: boolean;
}

