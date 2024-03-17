export interface UploadedFile {
  id: number;
  filename: string;
  path: string;
  checksum: string;
  creationDate: Date;
  createdBy: string;
  size: number;
}
