import { Payment } from 'src/app/modules/invoicing/model/Payment';
import { Debour } from './Debour';

export interface DebourPaymentAssociationRequest {
  payment: Payment;
  debours: Debour[];
}
