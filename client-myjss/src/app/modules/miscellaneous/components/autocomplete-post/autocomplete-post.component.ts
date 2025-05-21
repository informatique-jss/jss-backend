import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { MyJssCategory } from '../../../tools/model/MyJssCategory';
import { Post } from '../../../tools/model/Post';
import { MyJssCategoryService } from '../../../tools/services/myjss.category.service';
import { PostService } from '../../../tools/services/post.service';
import { PagedContent } from '../../model/PagedContent';
import { GenericAutocompleteComponent } from '../forms/generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-post',
  templateUrl: './autocomplete-post.component.html',
  styleUrls: ['./autocomplete-post.component.css'],
  standalone: false
})
export class AutocompletePostComponent extends GenericAutocompleteComponent<Post, Post> implements OnInit {

  @Input() myJssCategory: MyJssCategory | undefined;

  additionalCategory: MyJssCategory = { id: -1, name: 'Toutes les categories', slug: "all-categories", categoryOrder: 1 };
  autocompletePostForm = this.formBuild.group({});

  postResults: Post[] = [];
  searchText: string = "";

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
    if (!this.additionalCategory)
      this.myJssCategoryService.getMyJssCategories().subscribe(res => this.additionalCategory = res[0]);
  }

  changeMyJssCategory() {
    if (this.myJssCategory)
      this.onChangeMyJssCategory.next(this.myJssCategory);
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


  filterResultPosts(filteredTypes: any[], query: string) {
    var results: any[] = [];
    filteredTypes.forEach(
      (item) => {
        if ((item.titleText && item.titleText.toLowerCase().includes(query.toLowerCase())) ||
          (item.excerptText && item.excerptText.toLowerCase().includes(query.toLowerCase()))) {
          results.push(item);
        }
      }
    );
    return results;
  }

  highlightText(text: string): string {
    const regex = new RegExp(`(${this.searchText})`, 'gi');
    return text.replace(regex, `<span style="background-color: yellow;">$1</span>`);
  }
}
