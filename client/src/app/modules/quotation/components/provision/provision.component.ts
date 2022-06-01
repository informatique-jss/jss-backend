import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { IQuotation } from 'src/app/modules/quotation/model/IQuotation';
import { Affaire } from '../../model/Affaire';
import { Provision } from '../../model/Provision';

@Component({
  selector: 'provision',
  templateUrl: './provision.component.html',
  styleUrls: ['./provision.component.css']
})
export class ProvisionComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() quotation: IQuotation = {} as IQuotation;
  @Input() editMode: boolean = false;

  constructor(private formBuilder: FormBuilder,) { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.quotation != undefined) {
      // TODO : remove
      this.quotation.provisions = [] as Array<Provision>;
      this.quotation.provisions.push({} as Provision);
      this.quotation.provisions[0].affaire = {} as Affaire;

      if (this.quotation.provisions != null && this.quotation.provisions != undefined && this.quotation.provisions.length > 0)
        this.sortProvisions();
      this.provisionForm.markAllAsTouched();
    }
  }

  getFormStatus(): boolean {
    return this.provisionForm.valid;
  }

  provisionForm = this.formBuilder.group({
    obervations: ['', []],
  });

  sortProvisions() {
    this.quotation.provisions.sort((a: Provision, b: Provision) => {
      if (a.affaire == null && b.affaire != null)
        return 1;
      if (b.affaire != null && b.affaire == null)
        return -1;
      if (a.affaire == null && b.affaire == null)
        return 0;
      let nameA = "";
      let nameB = "";
      if (a.affaire.isIndividual) {
        nameA = (a.affaire.firstname != null ? a.affaire.firstname : "") + (a.affaire.lastname != null ? a.affaire.lastname : "");
      } else {
        nameA = a.affaire.denomination;
      }
      if (b.affaire.isIndividual) {
        nameB = (b.affaire.firstname != null ? b.affaire.firstname : "") + (b.affaire.lastname != null ? b.affaire.lastname : "");
      } else {
        nameB = b.affaire.denomination;
      }
      return nameA.localeCompare(nameB);
    })
  }

}

