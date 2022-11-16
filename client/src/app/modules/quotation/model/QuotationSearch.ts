import { QuotationStatus } from 'src/app/modules/quotation/model/QuotationStatus';
import { Employee } from "../../profile/model/Employee";
import { ITiers } from '../../tiers/model/ITiers';

export interface QuotationSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  salesEmployee: Employee;
  quotationStatus: QuotationStatus[];
  customerOrders: ITiers[];
}
