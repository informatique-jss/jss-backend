import { ChangeDetectorRef, Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { PROVISION_TYPE_DOMICILIATION_CODE, PROVISION_TYPE_SHAL_CODE } from 'src/app/libs/Constants';
import { Affaire } from '../../model/Affaire';
import { Provision } from '../../model/Provision';
import { ProvisionFamilyType } from '../../model/ProvisionFamilyType';
import { ProvisionType } from '../../model/ProvisionType';
import { ProvisionFamilyTypeService } from '../../services/provision.family.type.service';
import { ProvisionTypeService } from '../../services/provision.type.service';

@Component({
  selector: 'provision',
  templateUrl: './provision.component.html',
  styleUrls: ['./provision.component.css']
})
export class ProvisionComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() provisions: Provision[] = [] as Array<Provision>;
  @Input() editMode: boolean = false;

  provisionFamilyTypes: ProvisionFamilyType[] = [] as Array<ProvisionFamilyType>;
  provisionTypes: ProvisionType[] = [] as Array<ProvisionType>;

  filteredProvisions: Provision[] = [] as Array<Provision>;

  PROVISION_TYPE_DOMICILIATION_CODE = PROVISION_TYPE_DOMICILIATION_CODE;
  PROVISION_TYPE_SHAL_CODE = PROVISION_TYPE_SHAL_CODE;

  constructor(private formBuilder: FormBuilder,
    protected provisionFamilyTypeService: ProvisionFamilyTypeService,
    protected provisionTypeService: ProvisionTypeService,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.provisionFamilyTypeService.getProvisionFamilyTypes().subscribe(response => {
      this.provisionFamilyTypes = response;
    })
    this.provisionTypeService.getProvisionTypes().subscribe(response => {
      this.provisionTypes = response;
    })
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.provisions != undefined) {
      if (this.provisions != null && this.provisions != undefined && this.provisions.length > 0) {
        this.sortProvisions();
        this.filteredProvisions = this.provisions;
        this.provisions.forEach(provision => {
          if (provision.provisionType == null || provision.provisionType == undefined) {
            provision.provisionType = {} as ProvisionType;
          }
        })
        this.changeDetectorRef.detectChanges();

      }
      this.applyFilter(null);
      this.provisionForm.markAllAsTouched();
    }
  }

  getFormStatus(): boolean {
    return this.provisionForm.valid;
  }

  provisionForm = this.formBuilder.group({
    obervations: ['', []],
    provisionFamilyType: ['', [Validators.required]],
    provisionType: ['', [Validators.required]],
  });

  sortProvisions() {
    this.provisions.sort((a: Provision, b: Provision) => {
      if (a.affaire == null && b.affaire != null)
        return -1;
      if (b.affaire != null && b.affaire == null)
        return 1;
      if (a.affaire.id == null && b.affaire.id != null)
        return -1;
      if (b.affaire.id != null && b.affaire.id == null)
        return 1;
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

  applyFilter(filterValue: any) {
    if (filterValue == null || filterValue == undefined || filterValue.length == 0) {
      this.filteredProvisions = this.provisions;
      return;
    }
    this.filteredProvisions = [] as Array<Provision>;
    if (this.provisions != null && this.provisions != undefined) {
      this.provisions.forEach(provision => {
        const dataStr = JSON.stringify(provision).toLowerCase();
        if (dataStr.indexOf(filterValue.value.toLowerCase()) >= 0)
          this.filteredProvisions.push(provision);
      })
    }
  }

  createProvision() {
    if (this.provisions == null || this.provisions == undefined)
      this.provisions = [] as Array<Provision>;
    let provision = {} as Provision;
    provision.affaire = {} as Affaire;
    this.provisions.push(provision);
    this.sortProvisions();
    this.applyFilter(null);
  }

  deleteProvision(index: number) {
    if (this.filteredProvisions != null && this.filteredProvisions != undefined && this.filteredProvisions.length > 0) {
      for (let i = 0; i < this.provisions.length; i++) {
        const provision = this.provisions[i];
        if (JSON.stringify(provision).toLowerCase() == JSON.stringify(this.filteredProvisions[index]).toLowerCase())
          this.provisions.splice(i, 1);
      }
    }
    this.applyFilter(null);
  }

  compareWithId = compareWithId;

}

