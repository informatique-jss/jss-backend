import { KpiWidgetDto } from "./KpiWidgetDto";

export interface KpiCrm {
  id: number;
  title: string;
  kpiWidget: KpiWidgetDto;
  widgetOrder: number;
}
