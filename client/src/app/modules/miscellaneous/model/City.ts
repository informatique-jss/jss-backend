import { Country } from "./Country";
import { Department } from "./Department";

export interface City {
  id: number;
  code: string;
  label: string;
  postalCode: string;
  locality: string;
  department: Department;
  country: Country;
  isValidated: boolean;
}

