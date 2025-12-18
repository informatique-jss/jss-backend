export interface ResponsableDto {
  id: number;
  civility: string;
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
  isActive: boolean;
  function: string;
  mailRecipient: string;
  numberOfGiftPostsPerMonth: number;
  canViewAllTiersInWeb: boolean;
  observations: string;
}

