export interface TiersDto {
  id: number;
  denomination: string;
  lastname: string;
  firstname: string;
  kpiValues: Record<string, number>;
}
