import { ResponsableDto } from "../../tiers/model/ResponsableDto";
import { TiersDto } from "../../tiers/model/TiersDto";

export interface PaymentSearch {
    minAmount: number;
    maxAmount: number;
    label: string;
    startDate: Date;
    endDate: Date;
    tiers: TiersDto;
    responsable: ResponsableDto;
    isAssociated: boolean;
    isCancelled: boolean;
    isAppoint: boolean;
}
