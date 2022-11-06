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
  evenementCessation: string; // TODO : libre ou référentiel associé ?
  natureCessation: NatureCessation;
  succursaleOuFiliale: SuccursaleOuFiliale;
  formeExerciceActivitePrincipale: FormeExercice;
  indicateurPoursuiteCessation: boolean;
  tvaIntraCommunautaire: string;
  natureCreation: NatureCreation;
  personnePhysique: PersonnePhysique;
  personneMorale: PersonneMorale;
  exploitation: Exploitation;
  declarant: Declarant;
  piecesJointes: PiecesJointe[];
  indicateurActive: boolean;
}

