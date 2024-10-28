import { Country } from "./Country";
import { Department } from "./Department";

export interface City {
  id: number | undefined;
  code: string;
  label: string;
  postalCode: string;
  country: Country;
  department: Department;
}

