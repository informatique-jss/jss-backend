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

  getComments(page: number, size: number, commentSearch: CommentSearch, sortBy: string = 'creationDate', sortDir: string = 'desc'): Observable<PagedContent<Comment>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir)

    return this.postPagedList(params, "comments/search", commentSearch, "", "");
  }

  // TODO : une methode par modif
  addOrUpdateComment(comment: Comment, postId: number, parentCommentId?: number) {
    let params = new HttpParams()

    if (parentCommentId)
      params = params.set("parentCommentId", parentCommentId);

    return this.addOrUpdate(params.set("postId", postId), "post/comment/add", comment, "Votre commentaire a bien été posté !", "Une erreur s'est produite lors de l'enregistrement du commentaire, merci de réessayer");
  }


}
