import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { formatDateIso } from "../../../libs/FormatHelper";
import { AppRestService } from "../../main/services/appRest.service";
import { Attachment } from "../model/Attachment";
import { Service } from "../model/Service";


@Injectable({
  providedIn: 'root'
})
export class AttachmentService extends AppRestService<Attachment> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAttachmentsForProvisionOfService(service: Service) {
    return this.getList(new HttpParams().set("serviceId", service.id), "service/provision/attachments");
  }

  getAttachmentsForAffaire(idAffaire: number) {
    return this.getList(new HttpParams().set("idAffaire", idAffaire), "affaire/attachments");
  }

  deleteAttachment(idAttachment: number) {
    return this.get(new HttpParams().set("idAttachment", idAttachment), "attachments/delete", "Pièce-jointe supprimée", "Impossible de supprimer la pièce")
  }

  updateAttachmentDocumentDate(idAttachment: number, attachmentDate: Date) {
    return this.get(new HttpParams().set("idAttachment", idAttachment).set("attachmentDate", formatDateIso(new Date(attachmentDate))), "attachment/update/date");
  }

  getPurchaseOrderAttachment(idCustomerOrder: number) {
    return this.get(new HttpParams().set("idCustomerOrder", idCustomerOrder), "attachment/purchase-order");
  }
}
