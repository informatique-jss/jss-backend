import { Category } from "../main/model/Category";
import { JssCategory } from "../main/model/JssCategory";

export interface Constant {
  id: number;
  jssCategoryHomepageFirstHighlighted: JssCategory;
  jssCategoryHomepageSecondHighlighted: JssCategory;
  jssCategoryHomepageThirdHighlighted: JssCategory;
  categoryInterview: Category;
  categoryPodcast: Category;
  categoryArticle: Category;
  categorySerie: Category;
  categoryExclusivity: Category;

}
