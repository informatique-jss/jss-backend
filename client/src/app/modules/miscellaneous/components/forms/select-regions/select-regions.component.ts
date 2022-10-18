import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { Region } from '../../../model/Region';
import { RegionService } from '../../../services/region.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-regions',
  templateUrl: './select-regions.component.html',
  styleUrls: ['./select-regions.component.css']
})
export class SelectRegionsComponent extends GenericMultipleSelectComponent<Region> implements OnInit {

  types: Region[] = [] as Array<Region>;

  constructor(private formBuild: UntypedFormBuilder, private regionService: RegionService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.regionService.getRegions().subscribe(response => {
      this.types = response;
    })
  }

  displayLabel(object: any): string {
    return object ? (object.code + " - " + object.label) : '';
  }
}
