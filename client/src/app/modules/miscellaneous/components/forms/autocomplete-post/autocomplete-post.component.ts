import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { IndexEntity } from '../../../../../routing/search/IndexEntity';
import { IndexEntityService } from '../../../../../routing/search/index.entity.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-post',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompletePostComponent extends GenericAutocompleteComponent<IndexEntity, IndexEntity> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private indexEntityService: IndexEntityService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  @Input() onlyActive: boolean = true;

  searchEntities(value: string): Observable<IndexEntity[]> {
    return this.indexEntityService.getPostByKeyword(value);
  }

  displayLabel(post: IndexEntity): string {
    if (!post)
      return "";
    if (post.text) {
      let text = JSON.parse(post.text);
      let label = text.titleText;
      return label;
    }
    return "";
  }

  override  getPreviewActionLinkFunction(entity: IndexEntity): string[] | undefined {
    return undefined;
  }


}
