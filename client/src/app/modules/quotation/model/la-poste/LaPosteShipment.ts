import { LaPosteContext } from "./LaPosteContext";
import { LaPosteEvent } from "./LaPosteEvent";

export interface LaPosteShipment {
  id: number;
  product: string;
  holder: number;
  isFinal: boolean;
  entryDate: Date;
  deliveryDate: Date;
  event: LaPosteEvent[];
  contextData: LaPosteContext[];
}

