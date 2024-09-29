import { Author } from "./Author";
import { Content } from "./Content";
import { Media } from "./Media";
import { MyJssCategory } from "./MyJssCategory";
import { PublishingDepartment } from "./PublishingDepartment";
import { Tag } from "./Tag";

export interface Post {
  id: number;
  title: Content;
  excerpt: Content;
  date: Date;
  modified: Date;
  slug: string;
  sticky: boolean;
  content: Content;

  // Computed field
  fullAuthor: Author;
  fullCategories: MyJssCategory[];
  fullDepartment: PublishingDepartment[];
  fullTags: Tag[];
  media: Media;
  premium: boolean;
}


