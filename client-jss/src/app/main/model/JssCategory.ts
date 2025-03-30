import { Media } from "./Media";

export interface JssCategory {
  id: number;
  name: string;
  slug: string;
  color: string;
  picture: Media;
  count: number;
  categoryOrder: number;
}
