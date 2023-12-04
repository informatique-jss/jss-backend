import { Employee } from "src/app/modules/profile/model/Employee";

export interface IndexEntity {
  entityType: string;
  entityId: number;
  text: any;
  createdBy: Employee;
  updatedBy: Employee;
  udpatedDate: Date;
  createdDate: Date;
}
