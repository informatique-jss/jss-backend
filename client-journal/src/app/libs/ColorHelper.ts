import { Tag } from "../main/model/Tag";

export function getTagColorClass(tag: Tag): string {
  if (tag.id % 5 == 0)
    return "btn-primary-soft";
  if (tag.id % 5 == 1)
    return "btn-warning-soft";
  if (tag.id % 5 == 2)
    return "btn-success-soft";
  if (tag.id % 5 == 3)
    return "btn-danger-soft";
  if (tag.id % 5 == 4)
    return "btn-info-soft";
  return "btn-primary-soft";
}
