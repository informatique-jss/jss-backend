export interface KpiCrm {
  id: string;
  code: string;
  label: string;
  lastUpdate: string;
  unit: string;
  defaultValue: number;
  labelType: string;
  icon: string;
  isPositiveEvolutionGood: boolean;
  isToDisplayTiersMainPage: boolean;
  aggregateTypeForTimePeriod: string;
}
