import { Author } from "./Author";
import { Category } from "./Category";
import { Media } from "./Media";
import { MyJssCategory } from "./MyJssCategory";
import { PublishingDepartment } from "./PublishingDepartment";
import { Tag } from "./Tag";

export interface Post {
  id: number;
  titleText: string;
  excerptText: string;
  contentText: string;
  date: Date;
  modified: Date;
  slug: string;
  podcastUrl: string;
  videoUrl: string;
  sticky: boolean;
  relatedPosts: Post[];
  mediaTimeLength: number;

  // Computed field
  fullAuthor: Author;
  myJssCategories: MyJssCategory[];
  postCategories: Category[];
  departments: PublishingDepartment[];
  postTags: Tag[];
  media: Media;
  premium: boolean;
}
