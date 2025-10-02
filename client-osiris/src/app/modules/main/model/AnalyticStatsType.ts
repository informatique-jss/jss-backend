import { AnalyticStatsValue } from './AnalyticStatsValue';
export type AnalyticStatsType = {
  id: number,
  icon: string;
  analyticStatsValue: AnalyticStatsValue,
  title: string
  percentage: number;
  percentageIcon: string;
  isPositive: boolean;
  aggregateType: string;
  // valueDate: Date;
  // TODO aggregateType ?? ==>  reconvertir en J ou H dans le front les temps moyens
}
