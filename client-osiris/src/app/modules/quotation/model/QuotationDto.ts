
export interface QuotationDto {
  id: number;
  salesEmployee: string;
  creationDate: Date;
  status: string;
  responsablesIds: string;
  affaires: string[];
  services: string;
  origin: string;
  description: string;
  tiers: string;
  totalPrice: number;
}


