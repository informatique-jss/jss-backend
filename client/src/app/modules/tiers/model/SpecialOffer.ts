import { BillingItem } from './BillingItem';
export interface SpecialOffer {
  id: number;
  code: string;
  label: string;
  billingItems: BillingItem[];
}
