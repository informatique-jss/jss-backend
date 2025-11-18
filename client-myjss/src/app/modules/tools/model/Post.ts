import { Author } from "./Author";
import { Media } from "./Media";
import { MyJssCategory } from "./MyJssCategory";
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
  relatedPosts: Post[];
  mediaTimeLength: number;
  seoTitle: string;
  seoDescription: string;
  // Computed field
  fullAuthor: Author;
  myJssCategories: MyJssCategory[];
  postTags: Tag[];
  media: Media;
  isPremium: boolean;
  isBookmarked: boolean;
  postAdditionalAuthors: Author[];
}
