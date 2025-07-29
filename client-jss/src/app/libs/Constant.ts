import { Category } from "../main/model/Category";
import { JssCategory } from "../main/model/JssCategory";
import { PublishingDepartment } from "../main/model/PublishingDepartment";

export interface Constant {
  id: number;
  jssCategoryHomepageFirstHighlighted: JssCategory;
  jssCategoryHomepageSecondHighlighted: JssCategory;
  jssCategoryHomepageThirdHighlighted: JssCategory;
  publishingDepartmentIdf: PublishingDepartment;
  categoryInterview: Category;
  categoryPodcast: Category;
  categoryArticle: Category;
  categorySerie: Category;
  categoryExclusivity: Category;
}

export const globalConstantCache: {
  data: Constant | undefined;
} = { data: undefined };
