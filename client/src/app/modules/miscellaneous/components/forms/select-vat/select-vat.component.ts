import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { Vat } from '../../../model/Vat';
import { VatService } from '../../../services/vat.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-vat',
  templateUrl: './select-vat.component.html',
  styleUrls: ['./select-vat.component.css']
})
export class SelectVatComponent extends GenericSelectComponent<Vat> implements OnInit {

  types: Vat[] = [] as Array<Vat>;

  constructor(private formBuild: UntypedFormBuilder, private vatService: VatService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.vatService.getVats().subscribe(response => {
      this.types = response;
    })
  }
}
