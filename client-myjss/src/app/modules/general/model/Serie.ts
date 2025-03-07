import { Media } from "./Media";

export interface Serie {
  id: number;
  name: string;
  slug: string;
  count: number;
  serieOrder: number;
  titleText: string;
  excerptText: string;
  picture: Media;
}
