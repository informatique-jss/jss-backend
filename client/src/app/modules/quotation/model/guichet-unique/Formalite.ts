import { Content } from "./Content";
import { DiffusionINSEE } from "./referentials/DiffusionINSEE";
import { FormeJuridique } from "./referentials/FormeJuridique";
import { TypeFormalite } from "./referentials/TypeFormalite";
import { TypePersonne } from "./referentials/TypePersonne";

export interface Formalite {
  formalityDraftId: number;
  companyName: string;
  content: Content;
  referenceMandataire: string;
  nomDossier: string;
  signedPlace: string;
  typeFormalite: TypeFormalite;
  observationSignature: string;
  diffusionINSEE: DiffusionINSEE;
  indicateurEntreeSortieRegistre: boolean;
  typePersonne: TypePersonne;
  inscriptionOffice: boolean;
  inscriptionOfficePartnerCenter: string;
  hasRnippBeenCalled: boolean;
  indicateurNouvelleEntreprise: boolean;
  optionEIRL: boolean;
  optionME: boolean;
  formeJuridique: FormeJuridique;
  optionJqpaNumber: number;
  regularisation: boolean;
}

