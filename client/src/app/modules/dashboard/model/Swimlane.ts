export interface Swimlane<T> {
  rawLabel: string;
  label: string;
  status: T[];
  totalItems: number;
  isCollapsed: boolean;
}
