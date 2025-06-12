export interface Toast {
  message: string;
  title: string;
  isError: boolean;
  addedDateTime: Date;
  delay: number;
}
