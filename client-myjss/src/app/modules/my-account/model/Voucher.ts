import { Responsable } from "../../profile/model/Responsable";

export interface Voucher {
  id: number;
  code: string;
  startDate: Date;
  endDate: Date;
  discountAmount: number;
  discountRate: number;
  totalLimit: number;
  perUserLimit: number;
  responsables: Responsable[];
}
