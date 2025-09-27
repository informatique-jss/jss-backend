import { Country } from "./Country";

export interface City {
  id: number | undefined;
  code: string;
  label: string;
  postalCode: string;
  country: Country;
}

