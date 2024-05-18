import { Author } from "./Author";
import { Content } from "./Content";
import { Media } from "./Media";

export interface Page {
  id: number;
  author: number;
  fullAuthor: Author;
  date: Date;
  modified: Date;
  menu_order: number;
  parent: number;
  title: Content;
  slug: string;

  // Single page request fields
  content: Content;
  excerpt: Content;
  featured_media: number;
  media: Media;

  // Computed fields
  childrenPages: Page[];
  isPremium: boolean;
}
