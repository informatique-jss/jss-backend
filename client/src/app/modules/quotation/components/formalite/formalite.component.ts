import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { FORMALITE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { ConstantService } from '../../../miscellaneous/services/constant.service';
import { Content } from '../../model/guichet-unique/Content';
import { Formalite } from '../../model/guichet-unique/Formalite';
import { NatureCreation } from '../../model/guichet-unique/NatureCreation';
import { Provision } from '../../model/Provision';
import { ContentComponent } from '../guichet-unique/content/content.component';
import { NatureCreationComponent } from '../guichet-unique/nature-creation/nature-creation.component';

@Component({
  selector: 'formalite',
  templateUrl: './formalite.component.html',
  styleUrls: ['./formalite.component.css']
})
export class FormaliteComponent implements OnInit {

  @Input() formalite: Formalite = {} as Formalite;
  @Input() provision: Provision = {} as Provision;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Output() provisionChange: EventEmitter<void> = new EventEmitter<void>();
  @ViewChild(ContentComponent) contentComponent: ContentComponent | undefined;
  @ViewChild(NatureCreationComponent) natureCreationComponent: NatureCreationComponent | undefined;

  FORMALITE_ENTITY_TYPE = FORMALITE_ENTITY_TYPE;

  typePersonnePersonnePhysique = this.constantService.getTypePersonnePersonnePhysique();
  typePersonnePersonneMorale = this.constantService.getTypePersonnePersonneMorale();

  constructor(
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
  ) { }

  formaliteForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.formalite && !this.formalite.content)
      this.formalite.content = {} as Content;
    if (!this.formalite.content.natureCreation)
      this.formalite.content.natureCreation = {} as NatureCreation;
    if (!this.formalite.indicateurEntreeSortieRegistre)
      this.formalite.indicateurEntreeSortieRegistre = false;
    if (!this.formalite.hasRnippBeenCalled)
      this.formalite.hasRnippBeenCalled = false;
    if (!this.formalite.indicateurNouvelleEntreprise)
      this.formalite.indicateurNouvelleEntreprise = false;
    if (!this.formalite.optionEIRL)
      this.formalite.optionEIRL = false;
    if (!this.formalite.optionME)
      this.formalite.optionME = false;
    if (!this.formalite.regularisation)
      this.formalite.regularisation = false;
  }

  getFormStatus() {
    this.formaliteForm.markAllAsTouched();
    let status = this.formaliteForm.valid;
    if (this.contentComponent)
      status = status && this.contentComponent.getFormStatus();
    if (this.natureCreationComponent)
      status = status && this.natureCreationComponent.getFormStatus();
    return status;
  }

  provisionChangeFunction() {
    this.provisionChange.emit();
  }

}
