import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
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

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: UntypedFormBuilder, private vatService: VatService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.vatService.getVats().subscribe(response => {
      this.types = response;
    })
  }
}
