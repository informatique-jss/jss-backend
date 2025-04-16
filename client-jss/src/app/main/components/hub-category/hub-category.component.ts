import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from '../../../services/app.service';
import { JssCategory } from '../../model/JssCategory';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
import { PostService } from '../../services/post.service';
import { TagService } from '../../services/tag.service';
import { GenericHubComponent } from '../generic-hub/generic-hub.component';

@Component({
  selector: 'hub-category',
  templateUrl: './../generic-hub/generic-hub.component.html',
  styleUrls: ['./../generic-hub/generic-hub.component.css'],
  standalone: false
})
export class HubCategoryComponent extends GenericHubComponent<JssCategory> implements OnInit {

  @Input() override selectedEntityType: JssCategory | undefined;

  constructor(private postService: PostService, private tagService: TagService, appService: AppService, formBuilder: FormBuilder
  ) {
    super(appService, formBuilder);
  }
  override getAllPostByEntityType(selectedEntityType: JssCategory, page: number, pageSize: number, searchText: string): Observable<PagedContent<Post>> {
    return this.postService.getAllPostsByJssCategory(selectedEntityType, page, pageSize, searchText);
  }

  override getAllTagByEntityType(selectedEntityType: JssCategory): Observable<Array<Tag>> {
    return this.tagService.getAllTagsByJssCategory(selectedEntityType);
  }

  override getMostSeenPostByEntityType(selectedEntityType: JssCategory): Observable<Array<Post>> {
    return this.postService.getMostSeenPostByJssCatgory(selectedEntityType);
  }
}
