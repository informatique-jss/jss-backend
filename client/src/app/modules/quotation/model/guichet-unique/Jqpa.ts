import { PersonneQualifiee } from "./PersonneQualifiee";
import { CodeRolePersonneQualifiee } from "./referentials/CodeRolePersonneQualifiee";
import { OptionJQPA } from "./referentials/OptionJQPA";

export interface Jqpa {
  optionJQPA: OptionJQPA;
  indicateurActivitesMultiples: boolean;
  descriptionActivite: string;
  codeRolePersonneQualifiee: CodeRolePersonneQualifiee;
  libelleRolePersonneQualifiee: string;
  personneQualifiee: PersonneQualifiee;
  autreQualitePersonneQualifiee: string;
}

