


import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { UserNoteService } from 'src/app/services/user.notes.service';
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
* Filter results on authority type code provided
* Return all result if no family is provided
*/
  @Input() filteredCompetentAuthorityTypeCode: string | undefined;

  constructor(private formBuild: UntypedFormBuilder, private competentauthorityService: CompetentAuthorityService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<CompetentAuthority[]> {
    return this.competentauthorityService.getCompetentAuthorityByDepartmentAndName(value, this.modelDepartment);
  }

  mapResponse(response: CompetentAuthority[]): CompetentAuthority[] {
    if (!this.filteredCompetentAuthorityType && !this.filteredCompetentAuthorityTypeCode)
      return response;
    if (!response)
      return [] as Array<CompetentAuthority>;
    if (this.filteredCompetentAuthorityType)
      return response.filter(authority => authority.competentAuthorityType.code == this.filteredCompetentAuthorityType?.code);
    return response.filter(authority => authority.competentAuthorityType.code == this.filteredCompetentAuthorityTypeCode);
  }
}
