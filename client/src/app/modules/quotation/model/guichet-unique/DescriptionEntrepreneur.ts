import { CodeNationalite } from "./referentials/CodeNationalite";
import { FormeSociale } from "./referentials/FormeSociale";
import { Genre } from "./referentials/Genre";
import { QualiteDeNonSedentarite } from "./referentials/QualiteDeNonSedentarite";
import { Role } from "./referentials/Role";
import { SituationMatrimoniale } from "./referentials/SituationMatrimoniale";
import { StatutVisAVisFormalite } from "./referentials/StatutVisAVisFormalite";

export interface DescriptionEntrepreneur {
  siren: string;
  nicSiege: string;
  role: Role;
  dateEffetRoleDeclarant: Date;
  numeroSecu: string;
  nom: string;
  prenoms: string[];
  genre: Genre;
  titre: string;
  nomUsage: string;
  pseudonyme: string;
  dateDeNaissance: Date;
  paysNaissance: string;
  lieuDeNaissance: string;
  codePostalNaissance: string;
  codeInseeGeographique: string;
  codeNationalite: CodeNationalite;
  situationMatrimoniale: SituationMatrimoniale;
  optionRgpd: string; // TODO : comment remplir ?
  qualiteDeNonSedentarite: QualiteDeNonSedentarite;
  statutVisAVisFormalite: StatutVisAVisFormalite;
  formeSociale: FormeSociale;
  indicateurDeNonSedentarite: boolean;
  confirmRnippMismatch: boolean;
  dateEffet15P: Date;
  dateEffet10P: Date;
  dateEffet17P: Date;
  is10PTriggered: boolean;
  is15PTriggered: boolean;
  is17PTriggered: boolean;
}

