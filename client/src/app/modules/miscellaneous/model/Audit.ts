import { Employee } from "../../profile/model/Employee";

export interface Audit {
  id: number;
  entity: string;
  entityId: number;
  fieldName: string;
  oldValue: string;
  newValue: string;
  datetime: Date;
  username: string;
}
