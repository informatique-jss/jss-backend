import { AdresseDomicile } from "./AdresseDomicile";
import { Caracteristiques } from "./Caracteristiques";
import { Entreprise } from "./Entreprise";

export interface AdresseEntreprise {
  caracteristiques: Caracteristiques;
  adresse: AdresseDomicile;
  entrepriseDomiciliataire: Entreprise;
}

