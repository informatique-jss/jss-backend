import { Entreprise } from "./Entreprise";
import { Evenement } from "./Evenement";
import { GreffeImmat } from "./GreffeImmat";
import { IdentifiantFormalite } from "./IdentifiantFormalite";

export interface Item {
  id: string;
  identifiantFormalite: IdentifiantFormalite;
  entreprise: Entreprise;
  personnePhysique: null;
  greffeDestinataire: GreffeImmat;
  referenceTechnique: string;
  referenceClient: string;
  numeroLiasse: null | string;
  typeFormalite: string;
  evenements: Evenement[];
  evtCommentaire: null;
  suppressionPossible: boolean;
  reprisePossible: boolean;
  detailPossible: boolean;
  urlReprise: string;
  cloture: boolean;
  infosSpecifiquesFormaliteDpa: null;
}
