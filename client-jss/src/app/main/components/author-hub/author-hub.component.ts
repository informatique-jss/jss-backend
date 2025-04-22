import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
import { PostService } from '../../services/post.service';
import { TagService } from '../../services/tag.service';
import { GenericHubComponent } from '../generic-hub/generic-hub.component';

@Component({
  selector: 'author-hub',
  templateUrl: './../generic-hub/generic-hub.component.html',
  styleUrls: ['./../generic-hub/generic-hub.component.css'],
  standalone: false
})
export class AuthorHubComponent extends GenericHubComponent<Author> implements OnInit {

  @Input() override selectedEntityType: Author | undefined;

  constructor(private postService: PostService, private tagService: TagService, appService: AppService, formBuilder: FormBuilder
  ) {
    super(appService, formBuilder);
  }
  override getAllPostByEntityType(selectedEntityType: Author, page: number, pageSize: number, searchText: string): Observable<PagedContent<Post>> {
    return this.postService.getAllPostsByAuthor(selectedEntityType, page, pageSize, searchText);
  }

  override getAllTagByEntityType(selectedEntityType: Author): Observable<Array<Tag>> {
    return this.tagService.getAllTagsByAuthor(selectedEntityType);
  }

  override getMostSeenPostByEntityType(selectedEntityType: Author, page: number, pageSize: number): Observable<PagedContent<Post>> {
    return this.postService.getMostSeenPostByAuthor(selectedEntityType, page, pageSize);
  }
}
