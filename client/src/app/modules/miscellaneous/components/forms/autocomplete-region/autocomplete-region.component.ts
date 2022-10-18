import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { Region } from '../../../model/Region';
import { RegionService } from '../../../services/region.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-region',
  templateUrl: './autocomplete-region.component.html',
  styleUrls: ['./autocomplete-region.component.css']
})
export class AutocompleteRegionComponent extends GenericLocalAutocompleteComponent<Region> implements OnInit {

  types: Region[] = [] as Array<Region>;

  constructor(private formBuild: UntypedFormBuilder, private regionService: RegionService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  filterEntities(types: Region[], value: string): Region[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(region =>
      region.label != undefined && (region.label.toLowerCase().includes(filterValue)));
  }

  initTypes(): void {
    this.regionService.getRegions().subscribe(response => this.types = response);
  }
}
