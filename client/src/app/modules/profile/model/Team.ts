import { Employee } from "./Employee";

export interface Team {
  id: number;
  label: string;
  code: string;
  manager: Employee;
}
