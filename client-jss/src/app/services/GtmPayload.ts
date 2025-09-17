export interface PageInfo {
  type: string;
  name: string;
  website?: string;
}

export interface PageViewPayload extends BasePayload {
}

export interface BasePayload {
  user?: { id: number };
  page?: PageInfo;
}

export interface CtaClickPayload extends BasePayload {
  cta: { type: 'quotation' | 'order' | 'link'; label: string, objectId?: number };
}

export interface FormSubmitPayload extends BasePayload {
  form: { type: string };
}

export interface BeginCheckoutPayload extends BasePayload {
  business: { type: 'quotation' | 'order'; service: string };
}

export interface FileUploadPayload extends BasePayload {
  business: { type: 'quotation' | 'order'; order_id: number; documentType: string };
}

export interface PurchasePayload extends BasePayload {
  business: { type: 'quotation' | 'order', order_id: number; amount: number; service: string, is_draft: boolean };
}

export interface LogPayload extends BasePayload {
  type: 'login' | 'logout' | 'switch';
}

export interface AppointmentBookedPayload extends BasePayload {
  business: { rdv_type: string };
}
