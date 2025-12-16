import { Employee } from "../../profile/model/Employee";

export interface QuotationSearch {
  salesEmployee: Employee;
  mail: string;
  startDate: Date;
  endDate: Date;
}
