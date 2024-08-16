import { QuotationStatus } from 'src/app/modules/quotation/model/QuotationStatus';
import { Employee } from "../../profile/model/Employee";
import { Tiers } from '../../tiers/model/Tiers';
import { Affaire } from './Affaire';

export interface QuotationSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  salesEmployee: Employee;
  assignedToEmployee: Employee;
  quotationStatus: QuotationStatus[];
  customerOrders: Tiers[];
  affaires: Affaire[];
}
