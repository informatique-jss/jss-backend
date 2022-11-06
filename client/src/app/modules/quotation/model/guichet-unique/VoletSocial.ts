import { Activite } from './Activite';
import { Mineur } from "./Mineur";
import { JeuneAgriculteur } from "./referentials/JeuneAgriculteur";
import { NatureVoletSocial } from "./referentials/NatureVoletSocial";
import { OrganismeAssuranceMaladieActue } from "./referentials/OrganismeAssuranceMaladieActue";
import { SituationVisAVisMsa } from "./referentials/SituationVisAVisMsa";
import { StatutExerciceActiviteSimultan } from './referentials/StatutExerciceActiviteSimultan';

export interface VoletSocial {
  natureVoletSocial: NatureVoletSocial;
  dateEffetVoletSocial: string;
  situationVisAVisMsa: SituationVisAVisMsa;
  activiteAnterieureActivite: Activite;
  activiteAnterieureDateFin: Date;
  activiteAnterieureCodeGeo: string;
  activiteAnterieurePays: string;
  activiteAnterieureCommune: string;
  activiteAnterieureCodePostal: string;
  organismeAssuranceMaladieActuelle: OrganismeAssuranceMaladieActue;
  autreOrganisme: string;
  demandeAcre: boolean;
  activiteSimultanee: boolean;
  statutExerciceActiviteSimultanee: StatutExerciceActiviteSimultan;
  autreActiviteExercee: string;
  affiliationPamBiologiste: boolean;
  affiliationPamPharmacien: boolean;
  jeuneAgriculteur: JeuneAgriculteur;
  organismePension: string; // TODO : référentiel ?
  nonSalarieOuConjointBeneficiaireRsaRmi: string;
  choixOrganismeAssuranceMaladie: OrganismeAssuranceMaladieActue;
  departementOrganismeAssuranceMaladie: string;
  indicateurRegimeAssuranceMaladie: boolean;
  declarationMineur: boolean;
  nbMineursDeclares: number;
  activiteNonSalariee: boolean;
  indicateurActiviteAnterieure: boolean;
  ancienNumeroSiren: string;
  mineur: Mineur[];
}

