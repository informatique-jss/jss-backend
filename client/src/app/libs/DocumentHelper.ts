import { Document } from "../modules/miscellaneous/model/Document";
import { DocumentType } from "../modules/miscellaneous/model/DocumentType";
import { IDocument } from "../modules/miscellaneous/model/IDocument";
import { ITiers } from "../modules/tiers/model/ITiers";

export function getDocument(documentCode: string, entity: IDocument, DocumentTypes: DocumentType[]) {
  // Tiers not loaded
  if (entity == null || getDocumentType(documentCode, DocumentTypes).id == undefined)
    return {} as Document;

  // No document in Tiers
  if (entity.documents == null || entity.documents == undefined) {
    entity.documents = [] as Array<Document>;
    let doc = {} as Document;
    doc.documentType = getDocumentType(documentCode, DocumentTypes);
    entity.documents.push(doc);
    return entity.documents[0];
  }

  // Document currently exists
  if (entity.documents.length > 0) {
    for (let i = 0; i < entity.documents.length; i++) {
      const documentFound = entity.documents[i];
      if (documentFound.documentType.code == documentCode) {
        return documentFound;
      }
    }
  }

  // Document not exists, create it
  let doc = {} as Document;
  doc.documentType = getDocumentType(documentCode, DocumentTypes);
  entity.documents.push(doc);
  return entity.documents[entity.documents.length - 1];
}

export function getDocumentType(codeTypeDocument: string, DocumentTypes: DocumentType[]): DocumentType {
  if (DocumentTypes.length > 0) {
    for (let i = 0; i < DocumentTypes.length; i++) {
      const documentType = DocumentTypes[i];
      if (documentType.code == codeTypeDocument)
        return documentType;
    }
  }
  return {} as DocumentType;
}
