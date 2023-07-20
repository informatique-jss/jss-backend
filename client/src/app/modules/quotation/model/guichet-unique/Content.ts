import { ComptesAnnuels } from "../ComptesAnnuels";
import { Declarant } from "./Declarant";
import { Exploitation } from "./Exploitation";
import { NatureCreation } from "./NatureCreation";
import { PersonneMorale } from "./PersonneMorale";
import { PersonnePhysique } from "./PersonnePhysique";
import { PiecesJointe } from "./PiecesJointe";
import { FormeExercice } from './referentials/FormeExercice';
import { NatureCessation } from "./referentials/NatureCessation";
import { SuccursaleOuFiliale } from './referentials/SuccursaleOuFiliale';

export interface Content {
  evenementCessation: string; // TODO : où est le référentiel associé ?
  /*
   fermeture d'etablissement secondaire dans le premier cas et dans le second cela peut être disparition de la personne morale ou disparition suite a fusion
  */
  natureCessation: NatureCessation;
  succursaleOuFiliale: SuccursaleOuFiliale;
  formeExerciceActivitePrincipale: FormeExercice; // TODO : non trouvé sur le site ... dans quel cas l'afficher ?
  indicateurPoursuiteCessation: boolean;
  tvaIntraCommunautaire: string;
  natureCreation: NatureCreation;
  personnePhysique: PersonnePhysique;
  personneMorale: PersonneMorale;
  exploitation: Exploitation;
  declarant: Declarant;
  piecesJointes: PiecesJointe[];
  indicateurActive: boolean;
  comptesAnnuels: ComptesAnnuels;
}

