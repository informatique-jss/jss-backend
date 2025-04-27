export interface SwimlaneType<T> {
  label: string;
  fieldName: string;
  valueFonction: ((element: T) => string) | undefined;
  fieldValueFunction: ((element: T) => string) | undefined;
}
