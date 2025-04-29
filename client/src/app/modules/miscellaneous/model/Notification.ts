import { Employee } from "../../profile/model/Employee";
import { Affaire } from "../../quotation/model/Affaire";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Invoice } from "../../quotation/model/Invoice";
import { Provision } from "../../quotation/model/Provision";
import { Quotation } from "../../quotation/model/Quotation";
import { Service } from "../../quotation/model/Service";
import { Responsable } from "../../tiers/model/Responsable";
import { Tiers } from "../../tiers/model/Tiers";

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
  customerOrder: CustomerOrder | undefined;
  service: Service | undefined;
  provision: Provision | undefined;
  invoice: Invoice | undefined;
  quotation: Quotation | undefined;
  tiers: Tiers | undefined;
  responsable: Responsable | undefined;
  affaire: Affaire | undefined;
}
