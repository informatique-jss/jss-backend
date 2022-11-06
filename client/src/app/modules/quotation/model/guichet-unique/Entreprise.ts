import { AdresseDomicile } from "./AdresseDomicile";
import { IndividuRepresentant } from "./IndividuRepresentant";
import { FormeJuridique } from "./referentials/FormeJuridique";
import { Role } from "./referentials/Role";
import { RoleEntreprise } from "./referentials/RoleEntreprise";
import { StatutPourLaFormalite } from "./referentials/StatutPourLaFormalite";
import { TypeRepresentant } from "./referentials/TypeRepresentant";

export interface Entreprise {
  roleEntreprise: RoleEntreprise;
  pays: string;
  siren: string;
  registre: string;
  denomination: string;
  objet: string;
  formeJuridique: FormeJuridique;
  formeJuridiqueEtrangere: string;
  dateEffet: Date;
  numDetenteur: string;
  numExploitant: string;
  numRna: string;
  lieuRegistre: string;
  numGreffe: string;
  entreeSortieDesChampsDeRegistre: string; // TODO :comment renseinger ?
  autreIdentifiantEtranger: string;
  nicSiege: string;
  nomCommercial: string;
  typeRepresentant: TypeRepresentant;
  individuRepresentant: IndividuRepresentant;
  entrepriseRepresentant: String; // TODO : quel type ici ?
  adresseEntrepriseRepresentant: AdresseDomicile;
  role: Role;
  statutPourLaFormalite: StatutPourLaFormalite;
  codeApe: string;
  indicateurAssocieUnique: boolean;
  nomExploitation: string;
}

