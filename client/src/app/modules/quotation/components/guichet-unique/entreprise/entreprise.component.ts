import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Formalite } from '../../../model/guichet-unique/Formalite';
import { Identite } from '../../../model/guichet-unique/Identite';
import { PersonnePhysique } from '../../../model/guichet-unique/PersonnePhysique';
import { Provision } from '../../../model/Provision';

@Component({
  selector: 'entreprise',
  templateUrl: './entreprise.component.html',
  styleUrls: ['./entreprise.component.css']
})
export class EntrepriseComponent implements OnInit {

  @Input() formalite: Formalite = {} as Formalite;
  @Input() personnePhysique: PersonnePhysique = {} as PersonnePhysique;
  @Input() provision: Provision | undefined;;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();
  //@ViewChild(IdentiteComponent) identiteComponent: IdentiteComponent | undefined;

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
    return status;
  }

  provisionChangeFunction() {
    this.provisionChange.emit(this.provision);
  }
}
