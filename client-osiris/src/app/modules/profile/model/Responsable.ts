
export interface Responsable {
  id: number;
  firstname: string | null;
  lastname: string | null;
  address: string;
  addressComplement: string;
  postalCode: string;
  cedexComplement: string;
  documents: Document[];
}
