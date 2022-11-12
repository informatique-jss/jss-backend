import { QuotationStatus } from 'src/app/modules/quotation/model/QuotationStatus';
import { Employee } from "../../profile/model/Employee";

export interface QuotationSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  salesEmployee: Employee;
  quotationStatus: QuotationStatus[];
}
