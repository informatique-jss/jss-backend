import { Employee } from "../../profile/model/Employee";
import { Tiers } from "../../tiers/model/Tiers";
import { Affaire } from "./Affaire";
import { CustomerOrderStatus } from './CustomerOrderStatus';

export interface OrderingSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  salesEmployee: Employee;
  customerOrderStatus: CustomerOrderStatus[];
  customerOrders: Tiers[];
  affaire: Affaire;
  idCustomerOrder: number;
  idQuotation: number;
  idCustomerOrderParentRecurring: number;
  idCustomerOrderChildRecurring: number;
  isDisplayOnlyRecurringCustomerOrder: boolean;
  isDisplayOnlyParentRecurringCustomerOrder: boolean;
  recurringValidityDate: Date;
}
