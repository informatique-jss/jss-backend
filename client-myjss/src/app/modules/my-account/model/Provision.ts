import { Announcement } from "./Announcement";
import { Domiciliation } from "./Domiciliation";

export interface Provision {
  id: number;
  domiciliation: Domiciliation | undefined;
  announcement: Announcement | undefined;
}
