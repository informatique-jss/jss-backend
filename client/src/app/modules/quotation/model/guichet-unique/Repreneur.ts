import { AdresseDomicile } from "./AdresseDomicile";
import { Contact } from "./Contact";
import { DescriptionEntrepreneur } from "./DescriptionEntrepreneur";
import { DescriptionPersonne } from "./DescriptionPersonne";

export interface Repreneur {
  descriptionPersonne: DescriptionPersonne;
  descriptionEntrepreneur: DescriptionEntrepreneur;
  adresseDomicile: AdresseDomicile;
  contact: Contact;
  dateEffet: Date;
  is29Or30PTriggered: boolean;
}

