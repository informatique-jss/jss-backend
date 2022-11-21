import { Employee } from '../../profile/model/Employee';

export interface OsirisLog {
  id: number;
  className: string;
  methodName: string;
  stackTrace: string;
  currentUser: Employee;
  createdDateTime: Date;
  logType: string;
  isRead: boolean;
}
