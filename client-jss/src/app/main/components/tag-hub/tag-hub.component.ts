import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
import { PostService } from '../../services/post.service';
import { TagService } from '../../services/tag.service';
import { GenericHubComponent } from '../generic-hub/generic-hub.component';
import { GenericInputComponent } from '../generic-input/generic-input.component';

@Component({
  selector: 'tag-hub',
  templateUrl: './../generic-hub/generic-hub.component.html',
  styleUrls: ['./../generic-hub/generic-hub.component.css'],
  imports: [SHARED_IMPORTS, GenericInputComponent, NgbTooltipModule],
  standalone: true
})
export class TagHubComponent extends GenericHubComponent<Tag> implements OnInit {

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
