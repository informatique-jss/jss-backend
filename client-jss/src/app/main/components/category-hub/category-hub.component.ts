import { Component, EventEmitter, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { JssCategory } from '../../model/JssCategory';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
import { LoginService } from '../../services/login.service';
import { PostService } from '../../services/post.service';
import { TagService } from '../../services/tag.service';
import { BookmarkComponent } from '../bookmark/bookmark.component';
import { GenericHubComponent } from '../generic-hub/generic-hub.component';
import { GenericInputComponent } from '../generic-input/generic-input.component';
import { SelectJssCategoryComponent } from '../select-jss-category/select-jss-category.component';

@Component({
  selector: 'category-hub',
  templateUrl: './category-hub.component.html',
  styleUrls: ['./category-hub.component.css'],
  imports: [SHARED_IMPORTS, GenericInputComponent, NgbTooltipModule, BookmarkComponent, SelectJssCategoryComponent],
  standalone: true
})
export class CategoryHubComponent extends GenericHubComponent<JssCategory> implements OnInit {

  selectedCategory: JssCategory | undefined;
  @Output() jssCategoryChange = new EventEmitter<JssCategory>();

  override ngOnInit(): void {
    this.selectedCategory = this.selectedEntityType;
    super.ngOnInit();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedEntityType'] && !changes['selectedEntityType'].firstChange)
      this.selectedCategory = changes['selectedEntityType'].currentValue;

  }

  constructor(private tagService: TagService, postService: PostService, loginService: LoginService, appService: AppService, formBuilder: FormBuilder, activeRoute: ActivatedRoute
  ) {
    super(appService, formBuilder, activeRoute, postService, loginService,);
  }
  override getAllPostByEntityType(selectedEntityType: JssCategory, page: number, pageSize: number, searchText: string, isDisplayNewPosts: boolean): Observable<PagedContent<Post>> {
    return this.postService.getAllPostsByJssCategory(selectedEntityType, page, pageSize, searchText, isDisplayNewPosts);
  }

  override getAllTagByEntityType(selectedEntityType: JssCategory, isDisplayNewPosts: boolean): Observable<Array<Tag>> {
    return this.tagService.getAllTagsByJssCategory(selectedEntityType, isDisplayNewPosts);
  }

  override getMostSeenPostByEntityType(selectedEntityType: JssCategory, page: number, pageSize: number): Observable<PagedContent<Post>> {
    return this.postService.getMostSeenPostByJssCategory(selectedEntityType, page, pageSize);
  }

  override searchForPosts() {
    if (this.searchText || this.selectedCategory) {
      this.selectedEntityType = this.selectedCategory;
      this.jssCategoryChange.emit(this.selectedCategory);
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
