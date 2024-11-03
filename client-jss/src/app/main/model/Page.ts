import { Author } from "./Author";
import { Media } from "./Media";

export interface Page {
  id: number;
  fullAuthor: Author;
  date: Date;
  modified: Date;
  menu_order: number;
  parentPage: Page;
  titleText: string;
  contentText: string;
  excerptText: string;
  slug: string;
  media: Media;
  childrenPages: Page[];
  isPremium: boolean;
}
