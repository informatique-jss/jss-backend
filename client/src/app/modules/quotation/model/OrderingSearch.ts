import { Employee } from "../../profile/model/Employee";
import { QuotationStatus } from "./QuotationStatus";

export interface OrderingSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  salesEmployee: Employee;
  quotationStatus: QuotationStatus[];
}
