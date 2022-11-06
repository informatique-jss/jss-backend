import { AdresseDomicile } from "./AdresseDomicile";
import { Contact } from "./Contact";
import { ContratDAppui } from "./ContratDAppui";
import { Eirl } from "./Eirl";
import { Entrepreneur } from "./Entrepreneur";
import { Entreprise } from "./Entreprise";
import { Insaisissabilite } from "./Insaisissabilite";

export interface Identite {
  entreprise: Entreprise;
  entrepreneur: Entrepreneur;
  eirl: Eirl[];
  contratDAppuiDeclare: boolean;
  contratDAppui: ContratDAppui;
  insaisissabilite: Insaisissabilite;
  adresseCorrespondance: AdresseDomicile;
  contactCorrespondance: Contact;
  nomCorrespondance: string;
}

