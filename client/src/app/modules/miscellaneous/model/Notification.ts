import { Employee } from "../../profile/model/Employee";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Provision } from "../../quotation/model/Provision";
import { Service } from "../../quotation/model/Service";

export interface Notification {
  id: number;
  employee: Employee;
  isRead: boolean;
  createdBy: Employee;
  createdDateTime: Date;
  updatedBy: Employee;
  updatedDateTime: Date;
  notificationType: string;
  detail1: string;
  summary: string;
  showPopup: boolean;
  customerOrder: CustomerOrder;
  service: Service;
  provision: Provision;
}
