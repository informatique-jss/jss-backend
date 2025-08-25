import { LaPosteShipment } from "./LaPosteShipment";

export interface LaPosteTracking {
  id: number;
  idShip: string;
  lang: string;
  scope: string;
  shipment: LaPosteShipment[];
}
