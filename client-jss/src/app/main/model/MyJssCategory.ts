import { Media } from "./Media";

export interface MyJssCategory {
  id: number;
  name: string;
  slug: string;
  color: string;
  picture: Media;
  count: number;
}