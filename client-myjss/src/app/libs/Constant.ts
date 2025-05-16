import { AttachmentType } from "../modules/my-account/model/AttachmentType";
import { BillingLabelType } from "../modules/my-account/model/BillingLabelType";
import { DocumentType } from "../modules/my-account/model/DocumentType";
import { PaymentType } from "../modules/my-account/model/PaymentType";
import { Country } from "../modules/profile/model/Country";
import { Responsable } from "../modules/profile/model/Responsable";
import { ServiceFamilyGroup } from "../modules/quotation/model/ServiceFamilyGroup";
import { Category } from "../modules/tools/model/Category";
import { MyJssCategory } from "../modules/tools/model/MyJssCategory";

export interface Constant {
  id: number;
  paymentTypePrelevement: PaymentType;
  paymentTypeVirement: PaymentType;
  paymentTypeCB: PaymentType;
  paymentTypeEspeces: PaymentType;
  paymentTypeCheques: PaymentType;
  paymentTypeAccount: PaymentType;
  billingLabelTypeCodeAffaire: BillingLabelType;
  billingLabelTypeOther: BillingLabelType;
  billingLabelTypeCustomer: BillingLabelType;
  documentTypeDigital: DocumentType;
  documentTypePaper: DocumentType;
  documentTypeBilling: DocumentType;
  countryFrance: Country;
  responsableDummyCustomerFrance: Responsable;
  myJssCategoryAnnouncement: MyJssCategory;
  myJssCategoryFormality: MyJssCategory;
  serviceFamilyGroupAnnouncement: ServiceFamilyGroup;
  categoryExclusivity: Category;
  attachmentTypeApplicationCv: AttachmentType;
}
