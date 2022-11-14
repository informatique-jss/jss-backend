import { Employee } from "../../profile/model/Employee";

export interface Notification {
  id: number;
  employee: Employee;
  entityType: string;
  entityId: number;
  isRead: boolean;
  createdBy: Employee;
  createdDateTime: Date;
  notificationType: string;
  detail1: string;
  summary: string;
  showPopup: boolean;
}
