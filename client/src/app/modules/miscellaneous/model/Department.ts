import { IReferential } from "../../administration/model/IReferential";
import { Region } from "./Region";

export interface Department extends IReferential {
  region: Region;
}
