import { AdresseDomicile } from "./AdresseDomicile";
import { Entrepreneur } from "./Entrepreneur";
import { FormeJuridique } from "./referentials/FormeJuridique";
import { Role } from "./referentials/Role";
import { RoleEntreprise } from "./referentials/RoleEntreprise";
import { StatutPourLaFormalite } from "./referentials/StatutPourLaFormalite";
import { TypeRepresentant } from "./referentials/TypeRepresentant";

export interface EntrepriseMandataire {
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
  entreeSortieDesChampsDeRegistre: string;
  autreIdentifiantEtranger: string;
  nicSiege: string;
  nomCommercial: string;
  typeRepresentant: TypeRepresentant;
  individuRepresentant: Entrepreneur;
  entrepriseRepresentant: string; // TODO : quel référentiel ici?
  adresseEntrepriseRepresentant: AdresseDomicile;
  role: Role;
  statutPourLaFormalite: StatutPourLaFormalite;
  codeApe: string;
  indicateurAssocieUnique: boolean;
  nomExploitation: string;
}

