import { Employee } from '../../profile/model/Employee';
export interface UserReporting {
  id: number | null;
  employee: Employee;
  settings: string;
  dataset: string;
  name: string | null;
}
