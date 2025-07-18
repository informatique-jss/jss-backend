import { AttachmentType } from "../../my-account/model/AttachmentType";
import { BillingLabelType } from "../../my-account/model/BillingLabelType";
import { DocumentType } from "../../my-account/model/DocumentType";
import { PaymentType } from "../../my-account/model/PaymentType";
import { ServiceType } from "../../my-account/model/ServiceType";
import { Country } from "../../profile/model/Country";
import { Responsable } from "../../profile/model/Responsable";
import { DomiciliationContractType } from "../../quotation/model/DomiciliationContractType";
import { Language } from "../../quotation/model/Language";
import { MailRedirectionType } from "../../quotation/model/MailRedirectionType";
import { ServiceFamilyGroup } from "../../quotation/model/ServiceFamilyGroup";
import { Category } from "../../tools/model/Category";
import { MyJssCategory } from "../../tools/model/MyJssCategory";

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
  myJssCategoryDomiciliation: MyJssCategory;
  myJssCategoryApostille: MyJssCategory;
  myJssCategoryDocument: MyJssCategory;
  serviceFamilyGroupAnnouncement: ServiceFamilyGroup;
  categoryExclusivity: Category;
  attachmentTypeApplicationCv: AttachmentType;
  languageFrench: Language;
  mailRedirectionTypeOther: MailRedirectionType;
  domiciliationContractTypeRouteEmailAndMail: DomiciliationContractType;
  domiciliationContractTypeRouteMail: DomiciliationContractType;
  serviceTypeAnnualSubscription: ServiceType;
  serviceTypeEnterpriseAnnualSubscription: ServiceType;
  serviceTypeMonthlySubscription: ServiceType;
  serviceTypeKioskNewspaperBuy: ServiceType;
  serviceTypeUniqueArticleBuy: ServiceType;
  serviceFamilyGroupFormality: ServiceFamilyGroup;
  serviceFamilyGroupOther: ServiceFamilyGroup;
}
