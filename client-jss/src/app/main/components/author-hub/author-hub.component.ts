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
import { PostService } from '../../services/post.service';
import { TagService } from '../../services/tag.service';
import { GenericHubComponent } from '../generic-hub/generic-hub.component';
import { GenericInputComponent } from '../generic-input/generic-input.component';

@Component({
  selector: 'author-hub',
  templateUrl: './../generic-hub/generic-hub.component.html',
  styleUrls: ['./../generic-hub/generic-hub.component.css'],
  imports: [SHARED_IMPORTS, GenericInputComponent, NgbTooltipModule],
  standalone: true
})
export class AuthorHubComponent extends GenericHubComponent<Author> implements OnInit {

  constructor(private tagService: TagService, postService: PostService, appService: AppService, formBuilder: FormBuilder, activeRoute: ActivatedRoute
  ) {
    super(appService, formBuilder, activeRoute, postService);
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
