import { Employee } from "../../profile/model/Employee";

export const CUSTOM_USER_PREFERENCE = "CUSTOM_USER_PREFERENCE"
export const DEFAULT_USER_PREFERENCE = "DEFAULT_USER_PREFERENCE"

export interface UserPreference {
  id: number;
  employee: Employee;
  json: string;
  key: string;
}


