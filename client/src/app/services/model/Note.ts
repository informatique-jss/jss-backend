export interface Note {
  id: number;
  label: string | undefined;
  value: any;
  link: string | undefined;
  isHeader: boolean;
}
