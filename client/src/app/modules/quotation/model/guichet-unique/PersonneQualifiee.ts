import { Contact } from "./Contact";
import { DescriptionEntrepreneur } from "./DescriptionEntrepreneur";
import { DescriptionPersonne } from "./DescriptionPersonne";
import { Immeuble } from "./Immeuble";

export interface PersonneQualifiee {
  descriptionPersonne: DescriptionPersonne;
  descriptionEntrepreneur: DescriptionEntrepreneur;
  adresseDomicile: Immeuble;
  contact: Contact;
  dateEffet: Date;
  is29Or30PTriggered: boolean;
}

