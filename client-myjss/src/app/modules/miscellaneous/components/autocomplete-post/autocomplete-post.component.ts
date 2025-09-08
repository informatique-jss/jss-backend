import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { AutocompleteLibModule } from 'angular-ng-autocomplete';
import { Observable } from 'rxjs';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { TrustHtmlPipe } from '../../../../libs/TrustHtmlPipe';
import { MyJssCategory } from '../../../tools/model/MyJssCategory';
import { Post } from '../../../tools/model/Post';
import { MyJssCategoryService } from '../../../tools/services/myjss.category.service';
import { PostService } from '../../../tools/services/post.service';
import { PagedContent } from '../../model/PagedContent';
import { GenericAutocompleteComponent } from '../forms/generic-autocomplete/generic-autocomplete.component';
import { SelectMyJssCategoryComponent } from '../forms/select-myjss-category/select-myjss-category.component';

@Component({
  selector: 'autocomplete-post',
  templateUrl: './autocomplete-post.component.html',
  styleUrls: ['./autocomplete-post.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, SelectMyJssCategoryComponent, AutocompleteLibModule, TrustHtmlPipe]
})
export class AutocompletePostComponent extends GenericAutocompleteComponent<Post, Post> implements OnInit {

  @Input() myJssCategory: MyJssCategory | undefined;

  additionalCategory: MyJssCategory = { id: -1, name: 'Tout', slug: "all-categories", categoryOrder: 1 };

  postResults: Post[] = [];
  searchText: string = "";

  autocompletePostForm!: FormGroup;

  @ViewChild('autoComp') autoComp: any;

  /**
 * Fired when MyJssCategory is modified by user
 */
  @Output() onChangeMyJssCategory: EventEmitter<MyJssCategory> = new EventEmitter();

  constructor(private formBuild: UntypedFormBuilder,
    private postService: PostService,
    private myJssCategoryService: MyJssCategoryService) {
    super(formBuild)
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.autocompletePostForm = this.formBuild.group({});
    if (!this.additionalCategory)
      this.myJssCategoryService.getMyJssCategories().subscribe(res => this.additionalCategory = res[0]);
  }

  changeMyJssCategory() {
    if (this.myJssCategory)
      this.onChangeMyJssCategory.next(this.myJssCategory);
  }

  triggerSearch(search: string) {
    this.isLoading = true;
    this.autoComp.searchInput.nativeElement.value = search;
    this.searchEntities(search).subscribe(response => {
      this.isLoading = false;
      if (this.filteredTypes)
        this.filteredTypes = response.content

      setTimeout(() => {
        const inputEl = this.autoCompEl?.nativeElement?.querySelector('input');
        if (inputEl) {
          inputEl.focus();
          inputEl.dispatchEvent(new Event('input'));
        }
      }, 100);
    });
  }

  override ngAfterViewInit() {
    setTimeout(() => {
      const clearButton = this.autoCompEl.nativeElement.querySelector('i[aria-label="Close"]');
      if (clearButton) {
        clearButton.addEventListener('click', () => {
          this.clearField();
        });
      }

      const inputEl = this.autoCompEl?.nativeElement?.querySelector('input');
      if (inputEl) {
        inputEl.setAttribute('autocomplete', 'off');
        inputEl.setAttribute('autocorrect', 'off');
        inputEl.setAttribute('autocapitalize', 'off');
        inputEl.setAttribute('spellcheck', 'false');
        inputEl.setAttribute('autocomplete', 'nope');
        inputEl.classList.add('ps-5');
        inputEl.classList.add('left-rounded-1');
        inputEl.classList.add('border-input');
        if (this.isMandatory)
          inputEl.required = true;
      }

    }, 0);
  }

  searchEntities(value: string): Observable<PagedContent<Post>> {
    this.searchText = value;
    if (this.myJssCategory) {
      return this.postService.searchPostsByMyJssCategory(value, this.myJssCategory, this.page, this.pageSize);
    }
    else
      return this.postService.searchPostsByMyJssCategory(value, undefined, this.page, this.pageSize);

  }


  filterResultPosts(filteredTypes: Post[], query: string) {
    return filteredTypes;
  }

}
