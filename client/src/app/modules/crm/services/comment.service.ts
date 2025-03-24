import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Comment } from '../../crm/model/Comment';

@Injectable({
  providedIn: 'root'
})
export class CommentService extends AppRestService<Comment> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }

  getComments() {
    return this.getList(new HttpParams(), "comments");
  }

  addOrUpdateComment(comment: Comment) {
    return this.addOrUpdate(new HttpParams(), "comment/add", comment, "Commentaire bien ajouté", "Erreur lors de l'enregistrement");
  }

}
