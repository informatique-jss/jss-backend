import { Region } from "./Region";

export interface Department {
  id: number;
  code: string;
  label: string;
  region: Region;
}
