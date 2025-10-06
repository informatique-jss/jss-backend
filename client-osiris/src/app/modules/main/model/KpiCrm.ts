import { KpiWidget } from "./KpiWidget";

export interface KpiCrm {
  id: number;
  title: string;
  kpiWidget: KpiWidget;
  widgetOrder: number;
}
