import { Document } from "../modules/miscellaneous/model/Document";
import { DocumentType } from "../modules/miscellaneous/model/DocumentType";
import { IDocument } from "../modules/miscellaneous/model/IDocument";

export function getDocument(documentType: DocumentType, entity: IDocument) {
  // Tiers not loaded
  if (entity == null || documentType.id == undefined)
    return { isRecipientClient: false, isRecipientAffaire: false } as Document;

  // No document in Tiers
  if (entity.documents == null || entity.documents == undefined) {
    entity.documents = [] as Array<Document>;
    let doc = { isRecipientClient: false, isRecipientAffaire: false } as Document;
    doc.documentType = documentType;
    entity.documents.push(doc);
    return entity.documents[0];
  }

  // Document currently exists
  if (entity.documents.length > 0) {
    for (let i = 0; i < entity.documents.length; i++) {
      const documentFound = entity.documents[i];
      if (documentFound.documentType.id == documentType.id) {
        return documentFound;
      }
    }
  }

  // Document not exists, create it
  let doc = { isRecipientClient: false, isRecipientAffaire: false } as Document;
  doc.documentType = documentType;
  entity.documents.push(doc);
  return entity.documents[entity.documents.length - 1];
}

export function replaceDocument(documentType: DocumentType, entity: IDocument, newDocument: Document) {
  // Tiers not loaded
  if (entity == null || documentType.id == undefined)
    return;

  // No document in Tiers
  if (entity.documents == null || entity.documents == undefined) {
    return;
  }

  // Document currently exists
  if (entity.documents.length > 0) {
    for (let i = 0; i < entity.documents.length; i++) {
      let documentFound = entity.documents[i];
      if (documentFound.documentType.id == documentType.id) {
        entity.documents[i] = newDocument;
        return;
      }
    }
  }

  // Document not exists
  // return entity.documents.push(newDocument);
}
