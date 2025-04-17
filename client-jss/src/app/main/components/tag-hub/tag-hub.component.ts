import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from '../../../services/app.service';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
import { PostService } from '../../services/post.service';
import { TagService } from '../../services/tag.service';
import { GenericHubComponent } from '../generic-hub/generic-hub.component';

@Component({
  selector: 'app-tag-hub',
  templateUrl: './tag-hub.component.html',
  styleUrls: ['./tag-hub.component.css'],
  standalone: false
})
export class TagHubComponent extends GenericHubComponent<Tag> implements OnInit {
  @Input() override selectedEntityType: Tag | undefined;

  constructor(private postService: PostService, private tagService: TagService, appService: AppService, formBuilder: FormBuilder
  ) {
    super(appService, formBuilder);
  }
  override getAllPostByEntityType(selectedEntityType: Tag, page: number, pageSize: number, searchText: string): Observable<PagedContent<Post>> {
    return this.postService.getAllPostsByTag(selectedEntityType, page, pageSize, searchText);
  }

  override getAllTagByEntityType(selectedEntityType: Tag): Observable<Array<Tag>> {
    return this.tagService.getAllTagsByTag(selectedEntityType);
  }

  override getMostSeenPostByEntityType(selectedEntityType: Tag, page: number, pageSize: number): Observable<PagedContent<Post>> {
    return this.postService.getMostSeenPostByTag(selectedEntityType, page, pageSize);
  }
}
