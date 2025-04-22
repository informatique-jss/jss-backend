import { Author } from "./Author";

export interface Media {
  id: number;
  fullAuthor?: Author;
  date: Date;
  media_type: string;
  alt_text: string;
  file: string;
  urlFull: string;
  urlLarge: string;
  urlMedium: string;
  urlMediumLarge: string;
  urlThumbnail: string;
  length: number;
}
