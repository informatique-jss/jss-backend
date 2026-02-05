import { Employee } from "../../profile/model/Employee";
import { SimpleProvisionStatus } from "./SimpleProvisonStatus";

export interface ProvisionSearch {
  salesEmployee: Employee;
  formalisteEmployee: Employee;
  mail: string;
  startDate: Date;
  endDate: Date;
  responsables: number;
  provisionStatus: SimpleProvisionStatus;
  affaire: string;
  waitingCompetentAuthoritySearch: string;
  guichetUniqueStatus: string;
  infogreffeStatus: string;
}
