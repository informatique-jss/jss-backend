import { AnalyticStatsValue } from './AnalyticStatsValue';
export type AnalyticStatsType = {
  id: number,
  icon: string;
  analyticStatsValue: AnalyticStatsValue,
  title: string
  percentage: number;
  percentageIcon: string;
  isPositive: boolean;
  // valueDate: Date;
}
