import { LaPosteRemovalPoint } from "./LaPosteRemovalPoint";

export interface LaPosteContext {
  id: number;
  removalPoint: LaPosteRemovalPoint[];
  originCountry: string;
  arrivalCountry: string;
}

