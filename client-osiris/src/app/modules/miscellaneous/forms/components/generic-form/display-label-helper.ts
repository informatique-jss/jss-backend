import { Employee } from "../../../../profile/model/Employee";
import { SelectType } from "./select-form-helper";

export function displayLabel(object: any, type: SelectType) {
  if (type == "commercial" || type == "formaliste")
    return (object as Employee).firstname + " " + (object as Employee).lastname;
  if (object && object.label)
    return object.label;
  return "no label found";
}
