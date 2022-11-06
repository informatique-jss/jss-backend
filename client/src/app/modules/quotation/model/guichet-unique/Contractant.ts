import { AdresseDomicile } from './AdresseDomicile';
import { Contact } from './Contact';
import { DescriptionPersonne } from "./DescriptionPersonne";

export interface Contractant {
  descriptionPersonne: DescriptionPersonne;
  descriptionEntrepreneur: DescriptionPersonne;
  adresseDomicile: AdresseDomicile;
  contact: Contact;
  dateEffet: Date;
  is29Or30PTriggered: boolean;
}

