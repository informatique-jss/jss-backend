import { Item } from "./Item";

export interface RootObject {
  success: boolean;
  identifier: string;
  label: string;
  loadedAttr: string;
  items: Item[];
}
