import { AdresseDomicile } from "./AdresseDomicile";
import { CapaciteJuridique } from "./CapaciteJuridique";
import { Conjoint } from "./Conjoint";
import { Contact } from "./Contact";
import { DescriptionEntrepreneur } from "./DescriptionEntrepreneur";
import { DescriptionPersonne } from "./DescriptionPersonne";
import { RoleConjoint } from "./referentials/RoleConjoint";
import { RegimeMicroSocial } from "./RegimeMicroSocial";
import { VoletSocial } from "./VoletSocial";

export interface IndividuRepresentant {
  roleConjoint: RoleConjoint;
  conjoint: Conjoint;
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

