import { AdresseDomicile } from "./AdresseDomicile";
import { CapaciteJuridique } from "./CapaciteJuridique";
import { Contact } from "./Contact";
import { DescriptionEntrepreneur } from "./DescriptionEntrepreneur";
import { DescriptionPersonne } from "./DescriptionPersonne";
import { RegimeMicroSocial } from "./RegimeMicroSocial";
import { VoletSocial } from "./VoletSocial";

export interface Conjoint {
  capaciteJuridique: CapaciteJuridique;
  voletSocial: VoletSocial;
  regimeMicroSocial: RegimeMicroSocial;
  descriptionPersonne: DescriptionPersonne;
  descriptionEntrepreneur: DescriptionEntrepreneur;
  adresseDomicile: AdresseDomicile;
  contact: Contact;
  dateEffet: Date;
  is29Or30PTriggered: boolean;
}

