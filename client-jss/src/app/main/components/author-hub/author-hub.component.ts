import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
import { LoginService } from '../../services/login.service';
import { PostService } from '../../services/post.service';
import { TagService } from '../../services/tag.service';
import { BookmarkComponent } from '../bookmark/bookmark.component';
import { GenericHubComponent } from '../generic-hub/generic-hub.component';
import { GenericInputComponent } from '../generic-input/generic-input.component';

@Component({
  selector: 'author-hub',
  templateUrl: './../generic-hub/generic-hub.component.html',
  styleUrls: ['./../generic-hub/generic-hub.component.css'],
  imports: [SHARED_IMPORTS, GenericInputComponent, NgbTooltipModule, BookmarkComponent],
  standalone: true
})
export class AuthorHubComponent extends GenericHubComponent<Author> implements OnInit {

  constructor(private tagService: TagService, postService: PostService,
    loginService: LoginService, appService: AppService,
    formBuilder: FormBuilder, activeRoute: ActivatedRoute
  ) {
    super(appService, formBuilder, activeRoute, postService, loginService);
  }

  override getAllPostByEntityType(selectedEntityType: Author, page: number, pageSize: number, searchText: string, isDisplayNewPosts: boolean): Observable<PagedContent<Post>> {
    return this.postService.getAllPostsByAuthor(selectedEntityType, page, pageSize, searchText, isDisplayNewPosts);
  }

  override getAllTagByEntityType(selectedEntityType: Author, isDisplayNewPosts: boolean): Observable<Array<Tag>> {
    return this.tagService.getAllTagsByAuthor(selectedEntityType, isDisplayNewPosts);
  }

  override getMostSeenPostByEntityType(selectedEntityType: Author, page: number, pageSize: number): Observable<PagedContent<Post>> {
    return this.postService.getMostSeenPostByAuthor(selectedEntityType, page, pageSize);
  }
}
