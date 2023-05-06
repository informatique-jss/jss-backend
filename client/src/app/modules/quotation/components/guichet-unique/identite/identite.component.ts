import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Formalite } from '../../../model/Formalite';
import { Provision } from '../../../model/Provision';
import { Identite } from '../../../model/guichet-unique/Identite';
import { EntrepriseComponent } from '../entreprise/entreprise.component';

@Component({
  selector: 'identite',
  templateUrl: './identite.component.html',
  styleUrls: ['./identite.component.css']
})
export class IdentiteComponent implements OnInit {

  @Input() formalite: Formalite = {} as Formalite;
  @Input() provision: Provision | undefined;
  @Input() identite: Identite = {} as Identite;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();
  @ViewChild(EntrepriseComponent) entrepriseComponent: EntrepriseComponent | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
  ) { }

  formaliteForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    /*if (this.formalite && this.formalite.content && this.formalite.content.personnePhysique && this.formalite.content.personnePhysique.identite) {
      if (!this.identite.contratDAppuiDeclare)
        this.identite.contratDAppuiDeclare = false;
    }*/
  }

  getFormStatus() {
    this.formaliteForm.markAllAsTouched();
    let status = this.formaliteForm.valid;
    if (this.entrepriseComponent)
      status = status && this.entrepriseComponent.getFormStatus();
    return status;
  }

  provisionChangeFunction() {
    this.provisionChange.emit(this.provision);
  }

}
