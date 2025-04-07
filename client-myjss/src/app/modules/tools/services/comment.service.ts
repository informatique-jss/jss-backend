import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../../libs/appRest.service';
import { PagedContent } from '../../miscellaneous/model/PagedContent';
import { Comment } from '../model/Comment';


@Injectable({
  providedIn: 'root'
})
export class CommentService extends AppRestService<Comment> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getParentCommentsForPost(postId: number, page: number, size: number): Observable<PagedContent<Comment>> {
    let params = new HttpParams()
      .set('postId', postId.toString())
      .set('page', page.toString())
      .set('size', size.toString());

    return this.getPagedList(params, "post/comments", "", "");
  }

  addOrUpdateComment(comment: Comment, parentCommentId: number, postId: number) {
    let params = new HttpParams()

    if (parentCommentId)
      params = params.set("parentCommentId", parentCommentId);

    return this.addOrUpdate(params.set("postId", postId), "post/comment/add", comment, "Votre commentaire a bien été posté !", "Une erreur s'est produite lors de l'enregistrement du commentaire, merci de réessayer");
  }
}
