import { ActiveDirectoryGroup } from "../../miscellaneous/model/ActiveDirectoryGroup";
import { Employee } from "../../profile/model/Employee";
import { Responsable } from "../../tiers/model/Responsable";
import { CustomerOrder } from "./CustomerOrder";
import { Provision } from "./Provision";
import { Quotation } from "./Quotation";

export interface CustomerOrderComment {
  id: number;
  createdDateTime: Date;
  employee: Employee;
  currentCustomer: Responsable;
  comment: string;
  provision: Provision;
  customerOrder: CustomerOrder;
  quotation: Quotation;
  activeDirectoryGroups: ActiveDirectoryGroup[];
  isRead: boolean;
  isToDisplayToCustomer: boolean;
  // Only for frontend
  isCurrentUserInGroup: boolean;
}
