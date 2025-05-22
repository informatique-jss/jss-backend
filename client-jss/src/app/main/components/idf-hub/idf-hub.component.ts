import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from '../../../services/app.service';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { Tag } from '../../model/Tag';
import { PostService } from '../../services/post.service';
import { TagService } from '../../services/tag.service';
import { GenericHubComponent } from '../generic-hub/generic-hub.component';

@Component({
  selector: 'idf-hub',
  templateUrl: './../generic-hub/generic-hub.component.html',
  styleUrls: ['./../generic-hub/generic-hub.component.css'],
  standalone: false
})
export class IdfHubComponent extends GenericHubComponent<PublishingDepartment> implements OnInit {

  @Input() override selectedEntityType: PublishingDepartment = {} as PublishingDepartment;

  constructor(private postService: PostService, private tagService: TagService, appService: AppService, formBuilder: FormBuilder
  ) {
    super(appService, formBuilder);
  }

  override getAllPostByEntityType(selectedEntityType: PublishingDepartment, page: number, pageSize: number, searchText: string): Observable<PagedContent<Post>> {
    return this.postService.getAllPostsByIleDeFrance(page, pageSize, searchText);
  }

  override getAllTagByEntityType(selectedEntityType: PublishingDepartment): Observable<Array<Tag>> {
    return this.tagService.getAllTagsByIleDeFrance();
  }

  override getMostSeenPostByEntityType(selectedEntityType: PublishingDepartment, page: number, pageSize: number): Observable<PagedContent<Post>> {
    return this.postService.getMostSeenPostByIleDeFrance(page, pageSize);
  }


}
