import { Employee } from "../../profile/model/Employee";
import { ITiers } from '../../tiers/model/ITiers';
import { Affaire } from "./Affaire";
import { CustomerOrderStatus } from './CustomerOrderStatus';

export interface OrderingSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  salesEmployee: Employee;
  assignedToEmployee: Employee;
  customerOrderStatus: CustomerOrderStatus[];
  customerOrders: ITiers[];
  affaire: Affaire;
  idCustomerOrder: number;
  idQuotation: number;
  idCustomerOrderParentRecurring: number;
  idCustomerOrderChildRecurring: number;
  isDisplayOnlyRecurringCustomerOrder: boolean;
  isDisplayOnlyParentRecurringCustomerOrder: boolean;
  recurringValidityDate: Date;
}
