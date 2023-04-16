import { CartRate } from "./CartRate";

export interface Cart {
  id: number;
  cartRates: CartRate[];
  total: number;
  mipOrderNum: number;
  paymentDate: string;
  paymentType: string;
  status: string;
  created: string;
  updated: string;
}
