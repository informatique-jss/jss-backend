export interface ResponsableDto {
  id: number;
  lastname: string;
  firstname: string;
  kpiValues: Record<string, string>;
  tiersId: number;
  tiersDenomination: string;
  tiersCategory: string;
  responsableCategory: string;
  salesEmployee: string;
  formalisteEmployee: string;
  mail: string;
  phones: string[];
}

