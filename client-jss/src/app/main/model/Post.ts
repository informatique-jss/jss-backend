import { Author } from "./Author";
import { Category } from "./Category";
import { JssCategory } from "./JssCategory";
import { Media } from "./Media";
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
  isSticky: boolean;
  isBookmarked: boolean;
  relatedPosts: Post[];
  mediaTimeLength: number;

  // Computed field
  fullAuthor: Author;
  jssCategories: JssCategory[];
  postCategories: Category[];
  departments: PublishingDepartment[];
  postTags: Tag[];
  media: Media;
  isPremium: boolean;
}
