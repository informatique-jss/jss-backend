import { PaymentType } from "../modules/my-account/model/PaymentType";

export interface Constant {
  id: number;
  paymentTypePrelevement: PaymentType;
  paymentTypeVirement: PaymentType;
  paymentTypeCB: PaymentType;
  paymentTypeEspeces: PaymentType;
  paymentTypeCheques: PaymentType;
  paymentTypeAccount: PaymentType;
}