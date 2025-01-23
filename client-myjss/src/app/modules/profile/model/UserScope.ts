import { Responsable } from "./Responsable";

export interface UserScope {
  id: number;
  responsable: Responsable;
  responsableViewed: Responsable;
}
