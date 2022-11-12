import { Employee } from "../../profile/model/Employee";
import { CustomerOrderStatus } from './CustomerOrderStatus';

export interface OrderingSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  salesEmployee: Employee;
  customerOrderStatus: CustomerOrderStatus[];
}
