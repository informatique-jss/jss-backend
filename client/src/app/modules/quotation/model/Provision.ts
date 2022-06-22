import { Affaire } from "./Affaire";
import { Bodacc } from "./Bodacc";
import { Domiciliation } from "./Domiciliation";
import { ProvisionFamilyType } from "./ProvisionFamilyType";
import { ProvisionType } from "./ProvisionType";
import { Shal } from "./Shal";

export interface Provision {
  id: number;
  affaire: Affaire;
  domiciliation: Domiciliation | undefined;
  shal: Shal | undefined;
  bodacc: Bodacc | undefined;
  provisionFamilyType: ProvisionFamilyType;
  provisionType: ProvisionType;
}
