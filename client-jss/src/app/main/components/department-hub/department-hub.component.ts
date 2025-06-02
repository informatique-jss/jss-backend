import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { Tag } from '../../model/Tag';
import { PostService } from '../../services/post.service';
import { TagService } from '../../services/tag.service';
import { GenericHubComponent } from '../generic-hub/generic-hub.component';
import { GenericInputComponent } from '../generic-input/generic-input.component';
import { SelectPublishingDepartmentComponent } from '../select-publishing-department/select-publishing-department.component';

@Component({
  selector: 'department-hub',
  templateUrl: './department-hub.component.html',
  styleUrls: ['./department-hub.component.css'],
  imports: [SHARED_IMPORTS, GenericInputComponent, SelectPublishingDepartmentComponent, NgbTooltipModule],
  standalone: true
})
export class DepartmentHubComponent extends GenericHubComponent<PublishingDepartment> implements OnInit {
  isLoading: boolean = false;
  selectedPublishingDepartment: PublishingDepartment | undefined;
  publishingDepartments: PublishingDepartment[] = [];

  constructor(private tagService: TagService, postService: PostService, appService: AppService, formBuilder: FormBuilder, activeRoute: ActivatedRoute
  ) {
    super(appService, formBuilder, activeRoute, postService);
  }

  override ngOnInit(): void {
    this.selectedPublishingDepartment = this.selectedEntityType;
    super.ngOnInit();
  }

  override getAllPostByEntityType(selectedEntityType: PublishingDepartment, page: number, pageSize: number, searchText: string): Observable<PagedContent<Post>> {
    return this.postService.getAllPostsByPublishingDepartment(selectedEntityType, page, pageSize, searchText);
  }

  override getAllTagByEntityType(selectedEntityType: PublishingDepartment): Observable<Array<Tag>> {
    return this.tagService.getAllTagsByPublishingDepartment(selectedEntityType);
  }

  override getMostSeenPostByEntityType(selectedEntityType: PublishingDepartment, page: number, pageSize: number): Observable<PagedContent<Post>> {
    return this.postService.getMostSeenPostByPublishingDepartment(selectedEntityType, page, pageSize);
  }


  override searchForPosts() {
    if (this.searchText || this.selectedPublishingDepartment) {
      this.selectedEntityType = this.selectedPublishingDepartment;
      clearTimeout(this.debounce);
      this.searchResults = [];

      this.debounce = setTimeout(() => {
        this.fetchPosts(0);
        this.fetchTags();
        this.fetchMostSeenPosts();
      }, 500);
    }
  }
}
