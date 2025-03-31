import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { PagedContent } from 'src/app/services/model/PagedContent';
import { Comment } from '../../crm/model/Comment';
import { CommentSearch } from '../model/CommentSearch';

@Injectable({
  providedIn: 'root'
})
export class CommentService extends AppRestService<Comment> {


  constructor(http: HttpClient) {
    super(http, "crm");
  }

  getComments(page: number, size: number, commentSearch: CommentSearch): Observable<PagedContent<Comment>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.postPagedList(params, "comments/search", commentSearch, "", "");
  }

  getComment(commentId: number): Observable<Comment> {
    return this.get(new HttpParams().set("commentId", commentId), "comment/get", "", "");
  }

  updateContent(newContent: any, commentId: number) {
    return this.get(new HttpParams().set("newContent", newContent).set("commentId", commentId), "post/comment/content", "Modification prise en compte !", "Une erreur est survenue lors de l'enregistrement des modifications");
  }

  updateIsModerated(isModerated: boolean, commentId: number) {
    return this.get(new HttpParams().set("isModerated", isModerated).set("commentId", commentId), "post/comment/moderate", "Modification prise en compte !", "Une erreur est survenue lors de l'enregistrement des modifications");
  }

  updateIsDeleted(isDeleted: boolean, commentId: number) {
    return this.get(new HttpParams().set("isDeleted", isDeleted).set("commentId", commentId), "post/comment/delete", "Commentaire supprimé !", "Une erreur est survenue lors de la suppression du commentaire");
  }
}
