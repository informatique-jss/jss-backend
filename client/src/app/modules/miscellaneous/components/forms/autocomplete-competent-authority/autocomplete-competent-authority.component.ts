import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { CompetentAuthority } from '../../../model/CompetentAuthority';
import { CompetentAuthorityType } from '../../../model/CompetentAuthorityType';
import { Department } from '../../../model/Department';
import { CompetentAuthorityService } from '../../../services/competent.authority.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-competent-authority',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteCompetentAuthorityComponent extends GenericAutocompleteComponent<CompetentAuthority, CompetentAuthority> implements OnInit {

  /**
 * The model of department property.
 * If undefined, competent authorities are searched regardless the department
 */
  @Input() modelDepartment: Department | undefined;
  /**
 * Label to display.
 */
  @Input() label: string = "Label";
  /**
* Filter results on authority type provided
* Return all result if no family is provided
*/
  @Input() filteredCompetentAuthorityType: CompetentAuthorityType | undefined;
  /**
* Filter results on authority provided
* Return all result if no authority is provided
*/
  @Input() filteredCompetentAuthority: CompetentAuthority[] | undefined;

  constructor(private formBuild: UntypedFormBuilder, private competentauthorityService: CompetentAuthorityService,) {
    super(formBuild)
  }

  searchEntities(value: string): Observable<CompetentAuthority[]> {
    return this.competentauthorityService.getCompetentAuthorityByDepartmentAndName(value, this.modelDepartment);
  }

  mapResponse(response: CompetentAuthority[]): CompetentAuthority[] {
    if (!this.filteredCompetentAuthorityType && !this.filteredCompetentAuthority)
      return response;
    if (!response)
      return [] as Array<CompetentAuthority>;
    if (this.filteredCompetentAuthorityType)
      return response.filter(authority => authority.competentAuthorityType.id == this.filteredCompetentAuthorityType?.id);
    if (this.filteredCompetentAuthority)
      return response.filter(authority => {
        for (let competentAuthority of this.filteredCompetentAuthority!)
          if (competentAuthority.id == authority.id)
            return true;
        return false;
      });
    return [] as Array<CompetentAuthority>;
  }

  getPreviewActionLinkFunction(entity: CompetentAuthority): string[] | undefined {
    return ['administration/competent/authority/', entity.id + ""];
  }
}
