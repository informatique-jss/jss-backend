import { Author } from "./Author";

export interface Media {
  id: number;
  author: number;
  fullAuthor: Author;
  date: Date;
  media_type: string;
  alt_text: string;
  urlFull: string;
  urlLarge: string;
  urlMedium: string;
  urlMediumLarge: string;
  urlThumbnail: string;
}
