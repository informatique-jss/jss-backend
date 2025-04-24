import { TypeDocument } from "../modules/my-account/model/TypeDocument";


export function instanceOfTypeDocument(object: any): object is TypeDocument {
  if (object != null)
    return 'customLabel' in object;
  return false;
}

