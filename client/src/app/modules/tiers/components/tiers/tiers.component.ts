import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { isTiersTypeProspect } from 'src/app/libs/CompareHelper';
import { Tiers } from '../../model/Tiers';
import { TiersService } from '../../services/tiers.service';
import { DocumentManagementComponent } from '../document-management/document-management.component';
import { PrincipalComponent } from '../principal/principal.component';

@Component({
  selector: 'app-tiers',
  templateUrl: './tiers.component.html',
  styleUrls: ['./tiers.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class TiersComponent implements OnInit {

  tiers: Tiers = {} as Tiers;
  eventsTiersLoaded: Subject<void> = new Subject<void>();
  editMode: boolean = false;

  @ViewChild(PrincipalComponent) principalFormComponent: PrincipalComponent | undefined;
  @ViewChild(DocumentManagementComponent) documentManagementFormComponent: DocumentManagementComponent | undefined;

  constructor(private appService: AppService,
    private tiersService: TiersService,
    private snackBar: MatSnackBar) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Tiers / Responsables");

    this.tiersService.getTiers(306).subscribe(response => {
      this.tiers = response;
      this.eventsTiersLoaded.next();
    })
  }

  isTiersTypeProspect(): boolean {
    return isTiersTypeProspect(this.tiers);
  }

  saveTiers() {
    if (this.getFormsStatus())
      this.tiersService.addOrUpdateTiers(this.tiers).subscribe(response => {
        this.tiers = response;
        this.editMode = false;
      })
  }

  getFormsStatus(): boolean {
    console.log(this.tiers);
    let principalFormStatus = this.principalFormComponent?.getFormStatus();
    let documentManagementFormStatus = this.documentManagementFormComponent?.getFormStatus();
    let errorMessages: string[] = [] as Array<string>;
    if (!principalFormStatus)
      errorMessages.push("Onglet Principal");
    if (!documentManagementFormStatus)
      errorMessages.push("Onglet Gestion des pièces");
    if (errorMessages.length > 0) {
      let errorMessage = "Les onglets suivants ne sont pas correctement remplis. Veuillez les compléter avant de sauvegarder : " + errorMessages.join(" / ");
      let sb = this.snackBar.open(errorMessage, 'Fermer', {
        duration: 60 * 1000
      });
      sb.onAction().subscribe(() => {
        sb.dismiss();
      });
      return false;
    }
    return true;
  }

  editTiers() {
    this.editMode = true;
  }
}
