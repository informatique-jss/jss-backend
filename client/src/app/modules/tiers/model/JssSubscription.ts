import { Responsable } from "./Responsable";

export interface JssSubscription {
  id: number;
  responsable: Responsable;
  isPaperSubscription: boolean;
  isWebSubscription: boolean;
}
