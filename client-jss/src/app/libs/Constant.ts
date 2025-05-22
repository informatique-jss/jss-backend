import { JssCategory } from "../main/model/JssCategory";
import { PublishingDepartment } from "../main/model/PublishingDepartment";

export interface Constant {
  id: number;
  jssCategoryHomepageFirstHighlighted: JssCategory;
  jssCategoryHomepageSecondHighlighted: JssCategory;
  jssCategoryHomepageThirdHighlighted: JssCategory;
  publishingDepartmentIdf: PublishingDepartment;
}
