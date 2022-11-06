import { AncienExploitant } from "./AncienExploitant";
import { Contrat } from "./Contrat";
import { Publication } from "./Publication";
import { TypeOrigine } from "./referentials/TypeOrigine";

export interface Origine {
  typeOrigine: TypeOrigine;
  autreOrigine: string;
  contrat: Contrat;
  ancienExploitant: AncienExploitant;
  publication: Publication;
}

