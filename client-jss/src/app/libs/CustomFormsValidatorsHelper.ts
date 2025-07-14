
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

export function validateSiren(siren: string) {
  return String(siren).toUpperCase().replace(/\s/g, "")
    .match(/^(\d{9}|\d{3}[ ]\d{3}[ ]\d{3})$/);
};

export function validateSiret(siren: string) {
  return String(siren).toUpperCase().replace(/\s/g, "")
    .match(/^\d{14}$/);
};
export function validateRna(rna: string) {
  return String(rna).toUpperCase().replace(/\s/g, "")
    .match(/^[Ww]\d{9}$/);
};
