import { Entreprise } from "./Entreprise";
import { EvenementInfogreffe } from "./EvenementInfogreffe";
import { GreffeInfogreffe } from "./GreffeInfogreffe";
import { IdentifiantFormalite } from "./IdentifiantFormalite";
import { MontantFormalite } from "./MontantFormalite";

export interface FormaliteInfogreffe {
  id: string;
  identifiantFormalite: IdentifiantFormalite;
  entreprise: Entreprise;
  personnePhysique: string;
  greffeDestinataire: GreffeInfogreffe;
  referenceTechnique: string;
  referenceClient: string;
  numeroLiasse: string;
  typeFormalite: string;
  evenements: EvenementInfogreffe[];
  evtCommentaire: string;
  suppressionPossible: boolean;
  reprisePossible: boolean;
  detailPossible: boolean;
  urlReprise: string;
  cloture: boolean;
  infosSpecifiquesFormaliteDpa: string;
  montantFormalite: MontantFormalite;
  formaliteInfogreffe: FormaliteInfogreffe;

}
