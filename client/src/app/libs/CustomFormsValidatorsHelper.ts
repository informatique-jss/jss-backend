import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from "@angular/forms";

export function forbiddenQueryValidator(forbiddenWord: String): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    return control.value != undefined && control.value.toUpperCase().indexOf(forbiddenWord.toUpperCase()) >= 0 ? { forbiddenWord: { value: control.value } } : null;
  };
}

export function checkBothFieldAreTheSame(firstFieldName: string, secondFieldName: string): ValidationErrors | null {
  return (control: AbstractControl): ValidationErrors | null => {
    const root = control.root as FormGroup;

    const firstFieldValue = root.get(firstFieldName)?.value;
    const secondFieldValue = root.get(secondFieldName)?.value;
    if (((firstFieldValue == undefined || firstFieldValue == null || firstFieldValue.length == 0) && secondFieldValue != undefined && secondFieldValue != null && secondFieldValue.length > 0)
      || (secondFieldValue == undefined || secondFieldValue == null || secondFieldValue.length == 0) && firstFieldValue != undefined && firstFieldValue != null && firstFieldValue.length > 0)
      return {
        notBothField: true
      };
    return null;
  };
}

export function validateEmail(email: string) {
  return String(email)
    .toLowerCase()
    .match(
      /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
    );
};

export function validateFrenchPhone(phone: string) {
  return String(phone)
    .toLowerCase()
    .match(
      /^(?:(?:\+|00)33|0)\s*[1-9](?:[\s.-]*\d{2}){4}$/
    );
};

export function validateInternationalPhone(phone: string) {
  return String(phone)
    .toLowerCase()
    .match(
      /^(?:(?:\+|00)[1-9][1-9]|0)\s*[1-9](?:[\s.-]*\d{2}){4,6}$/
    );
};
