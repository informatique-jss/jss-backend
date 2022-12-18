import { QuotationStatus } from 'src/app/modules/quotation/model/QuotationStatus';
import { Employee } from "../../profile/model/Employee";
import { ITiers } from '../../tiers/model/ITiers';
import { Affaire } from './Affaire';

export interface QuotationSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  salesEmployee: Employee;
  quotationStatus: QuotationStatus[];
  customerOrders: ITiers[];
  affaires: Affaire[];
}
