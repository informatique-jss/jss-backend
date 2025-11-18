import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
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
  selector: 'tendency-hub',
  templateUrl: './../generic-hub/generic-hub.component.html',
  styleUrls: ['./../generic-hub/generic-hub.component.css'],
  imports: [SHARED_IMPORTS, GenericInputComponent, NgbTooltipModule, BookmarkComponent],
  standalone: true
})
export class TendencyHubComponent extends GenericHubComponent<{ id: number }> implements OnInit {
  constructor(postService: PostService,
    private tagService: TagService,
    appService: AppService,
    formBuilder: FormBuilder,
    activeRoute: ActivatedRoute,
    loginService: LoginService,
    router: Router
  ) {
    super(appService, formBuilder, activeRoute, postService, loginService, router);
  }
  override getAllPostByEntityType(selectedEntityType: Post, page: number, pageSize: number, searchText: string): Observable<PagedContent<Post>> {
    return this.postService.getPostsTendency(page, pageSize, searchText);
  }

  override getAllTagByEntityType(selectedEntityType: Post): Observable<Array<Tag>> {
    return this.tagService.getAllTendencyTags();
  }

  override getMostSeenPostByEntityType(selectedEntityType: Post, page: number, pageSize: number): Observable<PagedContent<Post>> {
    return this.postService.getMostSeenPosts(page, pageSize, "");
  }
}
