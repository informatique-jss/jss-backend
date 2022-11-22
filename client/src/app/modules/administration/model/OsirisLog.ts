import { Employee } from '../../profile/model/Employee';

export interface OsirisLog {
  id: number;
  className: string;
  methodName: string;
  message: string;
  stackTrace: string;
  currentUser: Employee;
  createdDateTime: Date;
  logType: string;
  isRead: boolean;
}
