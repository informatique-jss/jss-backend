export interface PageInfo {
  type: string;
  name: string;
}

export interface PageViewPayload extends BasePayload {
  page: PageInfo;
}

export interface BasePayload {
  user?: { id: number };
}

export interface CtaClickPayload extends BasePayload {
  cta: { type: 'quotation' | 'order' | 'link'; label: string, idService?: number };
  page?: PageInfo;
}

export interface FormSubmitPayload extends BasePayload {
  form: { type: string };
  page?: PageInfo;
}

export interface BeginCheckoutPayload extends BasePayload {
  business: { type: 'quotation' | 'order'; service: string };
  page?: PageInfo;
}

export interface FileUploadPayload extends BasePayload {
  business: { type: 'quotation' | 'order'; order_id: number; documentType: string };
  page?: PageInfo;
}

export interface PurchasePayload extends BasePayload {
  business: { type: 'quotation' | 'order', order_id: number; amount: number; service: string, is_draft: boolean };
  page?: PageInfo;
}

export interface LogPayload extends BasePayload {
  type: 'login' | 'logout' | 'switch';
  page?: PageInfo;
}

export interface AppointmentBookedPayload extends BasePayload {
  business: { order_id: string; rdv_type: string };
  page?: PageInfo;
}
