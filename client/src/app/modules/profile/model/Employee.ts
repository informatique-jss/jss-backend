
export interface Employee {
  id: number;
  firstname: string;
  lastname: string;
  username: string;
  title: string;
  mail: string;
  isActive: boolean;
  backups: Employee[];
  adPath: string;
  notificationTypeToHide: string[];
}
