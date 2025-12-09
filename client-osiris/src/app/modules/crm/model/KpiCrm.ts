import { KpiCrmCategory } from './KpiCrmCategory';
export interface KpiCrm {
  id: string;
  code: string;
  label: string;
  lastUpdate: string;
  unit: string;
  defaultValue: number;
  labelType: string;
  kpiCrmCategory: KpiCrmCategory;
  icon: string;
  isPositiveEvolutionGood: boolean;
  isToDisplayTiersMainPage: boolean;
  aggregateTypeForTimePeriod: string;
}
