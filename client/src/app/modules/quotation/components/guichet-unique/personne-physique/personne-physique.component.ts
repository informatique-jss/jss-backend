import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Formalite } from '../../../model/guichet-unique/Formalite';
import { Identite } from '../../../model/guichet-unique/Identite';
import { PersonnePhysique } from '../../../model/guichet-unique/PersonnePhysique';
import { Provision } from '../../../model/Provision';
import { IdentiteComponent } from '../identite/identite.component';

@Component({
  selector: 'personne-physique',
  templateUrl: './personne-physique.component.html',
  styleUrls: ['./personne-physique.component.css']
})
export class PersonnePhysiqueComponent implements OnInit {

  @Input() formalite: Formalite = {} as Formalite;
  @Input() personnePhysique: PersonnePhysique = {} as PersonnePhysique;
  @Input() provision: Provision = {} as Provision;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Output() provisionChange: EventEmitter<void> = new EventEmitter<void>();
  @ViewChild(IdentiteComponent) identiteComponent: IdentiteComponent | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
  ) { }

  formaliteForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.formalite && this.personnePhysique) {
      if (!this.personnePhysique.identite)
        this.personnePhysique.identite = {} as Identite;
    }
  }

  getFormStatus() {
    this.formaliteForm.markAllAsTouched();
    let status = this.formaliteForm.valid;
    if (this.identiteComponent)
      status = status && this.identiteComponent.getFormStatus();
    return status;
  }

  provisionChangeFunction() {
    this.provisionChange.emit();
  }
}
