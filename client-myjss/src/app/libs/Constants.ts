import { EntityType } from "../modules/miscellaneous/model/EntityType";

// Quotation status
export const QUOTATION_STATUS_OPEN = "OPEN";
export const QUOTATION_STATUS_ABANDONED = "ABANDONED";
export const QUOTATION_STATUS_QUOTATION_WAITING_CONFRERE = "QUOTATION_WAITING_CONFRERE";
export const QUOTATION_STATUS_TO_VERIFY = "TO_VERIFY";
export const QUOTATION_STATUS_REFUSED_BY_CUSTOMER = "REFUSED_BY_CUSTOMER";
export const QUOTATION_STATUS_SENT_TO_CUSTOMER = "SENT_TO_CUSTOMER";
export const QUOTATION_STATUS_VALIDATED_BY_CUSTOMER = "VALIDATED_BY_CUSTOMER";

// Customer order status
export const CUSTOMER_ORDER_STATUS_OPEN = "OPEN";
export const CUSTOMER_ORDER_STATUS_ABANDONED = "ABANDONED";
export const CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT = "WAITING_DEPOSIT";
export const CUSTOMER_ORDER_STATUS_TO_BILLED = "TO_BILLED";
export const CUSTOMER_ORDER_STATUS_BEING_PROCESSED = "BEING_PROCESSED";
export const CUSTOMER_ORDER_STATUS_BILLED = "BILLED";
export const CUSTOMER_ORDER_STATUS_PAYED = "PAYED";

// Service field types
export const SERVICE_FIELD_TYPE_INTEGER = "SERVICE_FIELD_TYPE_INTEGER";
export const SERVICE_FIELD_TYPE_TEXT = "SERVICE_FIELD_TYPE_TEXT";
export const SERVICE_FIELD_TYPE_TEXTAREA = "SERVICE_FIELD_TYPE_TEXTAREA";
export const SERVICE_FIELD_TYPE_DATE = "SERVICE_FIELD_TYPE_DATE";
export const SERVICE_FIELD_TYPE_SELECT = "SERVICE_FIELD_TYPE_SELECT";

export const MAX_SIZE_UPLOAD_FILES = 10485760;
export const INVOICING_PAYMENT_LIMIT_REFUND_EUROS: number = 2;
export const ASSO_SERVICE_DOCUMENT_ENTITY_TYPE: EntityType = { entityType: 'AssoServiceDocument', tabName: 'Documents du service', entryPoint: 'quotation/service' };

export const JSS_IBAN = "FR76 3000 4007 9900 0257 1438 960"
export const JSS_BIC = "BNPAFRPPXXX"