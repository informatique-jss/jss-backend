import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../main/services/appRest.service";
import { ReadingFolder } from '../model/ReadingFolder';

@Injectable({
  providedIn: 'root'
})
export class ReadingFolderService extends AppRestService<ReadingFolder> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getReadingFolders() {
    return this.getList(new HttpParams(), "reading-folders");
  }

  getReadingFolder(idReadingFolder: number) {
    return this.get(new HttpParams().set("idReadingFolder", idReadingFolder), "reading-folder");
  }

  createReadingFolder(readingFolder: ReadingFolder) {
    return this.postItem(new HttpParams(), "reading-folder", readingFolder);
  }

  deleteReadingFolder(readingFolder: ReadingFolder) {
    return this.get(new HttpParams().set("idReadingFolder", readingFolder.id), "reading-folder/delete");
  }

  getFirstPostImageForReadingFolder(readingFolder: ReadingFolder) {
    return this.get(new HttpParams().set("idReadingFolder", readingFolder.id), "reading-folder/image");
  }
}
