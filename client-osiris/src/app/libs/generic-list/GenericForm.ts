import { ValidatorFn } from "@angular/forms";
import { AutocompleteType } from "../../modules/miscellaneous/forms/components/autocomplete/autocomplete.component";
import { FormType, InputType } from "../../modules/miscellaneous/forms/components/generic-form/generic-form.component";
import { SelectType } from "../../modules/miscellaneous/forms/components/generic-form/select-form-helper";

export interface GenericForm<U> {
  label: string;
  type: FormType;
  inputType?: InputType;
  selectType?: SelectType;
  autocompleteType?: AutocompleteType;
  validators?: ValidatorFn[];
  errorMessages?: Record<string, string>;
}
