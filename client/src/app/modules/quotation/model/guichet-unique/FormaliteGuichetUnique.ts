import { Cart } from "./Cart";
import { Content } from "./Content";
import { DiffusionINSEE } from "./referentials/DiffusionINSEE";
import { FormeJuridique } from "./referentials/FormeJuridique";
import { TypeFormalite } from "./referentials/TypeFormalite";
import { TypePersonne } from "./referentials/TypePersonne";

export interface FormaliteGuichetUnique {
  id: number;
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
  hasRnippBeenCalled: boolean; // TODO : à déplacer dans le statut de la formalité  ?
  indicateurNouvelleEntreprise: boolean; // TODO : à déplacer dans le statut de la formalité  dans le cas où elle revient en erreur pour homonymie ?
  optionEIRL: boolean;
  optionME: boolean; // TODO : c'est quoi ?
  formeJuridique: FormeJuridique;
  optionJqpaNumber: number; // TODO : à mettre à côté de la liste des activités plutôt qu'en page principale / applicable que dans le cas d'une société ou une personne physique dépendant de la chambre des métiers en plus du greffe
  regularisation: boolean;
  carts: Cart[];
}
