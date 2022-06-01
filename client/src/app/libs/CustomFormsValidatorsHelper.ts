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

export function validateVat(vatNumber: string) {
  return String(vatNumber).toUpperCase().replace(/\s/g, "")
    .match(/^((AT)?U[0-9]{8}|(BE)?0[0-9]{9}|(BG)?[0-9]{9,10}|(CY)?[0-9]{8}L|(CZ)?[0-9]{8,10}|(DE)?[0-9]{9}|(DK)?[0-9]{8}|(EE)?[0-9]{9}|(EL|GR)?[0-9]{9}|(ES)?[0-9A-Z][0-9]{7}[0-9A-Z]|(FI)?[0-9]{8}|(FR)?[0-9A-Z]{2}[0-9]{9}|(GB)?([0-9]{9}([0-9]{3})?|[A-Z]{2}[0-9]{3})|(HU)?[0-9]{8}|(IE)?[0-9]S[0-9]{5}L|(IT)?[0-9]{11}|(LT)?([0-9]{9}|[0-9]{12})|(LU)?[0-9]{8}|(LV)?[0-9]{11}|(MT)?[0-9]{8}|(NL)?[0-9]{9}B[0-9]{2}|(PL)?[0-9]{10}|(PT)?[0-9]{9}|(RO)?[0-9]{2,10}|(SE)?[0-9]{12}|(SI)?[0-9]{8}|(SK)?[0-9]{10})$/);
};

export function validateSiren(siren: string) {
  return String(siren).toUpperCase().replace(/\s/g, "")
    .match(/^(\d{9}|\d{3}[ ]\d{3}[ ]\d{3})$/);
};

export function validateSiret(siren: string) {
  return String(siren).toUpperCase().replace(/\s/g, "")
    .match(/^\d{14}$/);
};
export function validateRna(rna: string) {
  console.log("tt");
  return String(rna).toUpperCase().replace(/\s/g, "")
    .match(/^[Ww]\d{9}$/);
};
