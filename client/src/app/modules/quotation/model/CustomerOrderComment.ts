import { ActiveDirectoryGroup } from "../../miscellaneous/model/ActiveDirectoryGroup";
import { Employee } from "../../profile/model/Employee";
import { CustomerOrder } from "./CustomerOrder";
import { Provision } from "./Provision";
import { Quotation } from "./Quotation";

export interface CustomerOrderComment {
  id: number;
  createdDateTime: Date;
  employee: Employee;
  comment: string;
  provision: Provision;
  customerOrder: CustomerOrder;
  quotation: Quotation;
  activeDirectoryGroups: ActiveDirectoryGroup[];

  // Only for frontend
  isCurrentUserInGroup: boolean;
}
