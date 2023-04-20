import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";

export interface OwncloudGreffeFile {
  id: number;
  filename: string;
  competentAuthority: CompetentAuthority;
}

