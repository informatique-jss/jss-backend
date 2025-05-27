import { Component, OnInit } from '@angular/core';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { Responsable } from '../../../profile/model/Responsable';
import { Tiers } from '../../../profile/model/Tiers';
import { UserScope } from '../../../profile/model/UserScope';
import { LoginService } from '../../../profile/services/login.service';
import { ResponsableService } from '../../../profile/services/responsable.service';
import { UserScopeService } from '../../../profile/services/user.scope.service';

@Component({
  selector: 'scope',
  templateUrl: './scope.component.html',
  styleUrls: ['./scope.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class ScopeComponent implements OnInit {

  potentialResponsables: Responsable[] | undefined;
  allTiers: Tiers[] = [];
  currentScope: UserScope[] | undefined;
  responsableStatus: boolean[] = [];
  currentUser: Responsable | undefined;
  isLoading: boolean = false;

  constructor(
    private responsableService: ResponsableService,
    private loginService: LoginService,
    private userScopeService: UserScopeService
  ) { }

  capitalizeName = capitalizeName;

  ngOnInit() {
    this.loginService.getCurrentUser().subscribe(response => this.currentUser = response);
    this.initCurrentScope();
  }

  initCurrentScope() {
    this.isLoading = true;
    this.responsableService.getPotentialUserScope().subscribe(response => {
      this.potentialResponsables = response;

      if (this.potentialResponsables && this.potentialResponsables.length > 0)
        for (let potentialResponsable of this.potentialResponsables)
          if (!this.tiersExistsInAllTiers(potentialResponsable.tiers))
            this.allTiers.push(potentialResponsable.tiers);

      this.allTiers.sort((a: Tiers, b: Tiers) => this.getTiersLabel(a).localeCompare(this.getTiersLabel(b)));
      this.isLoading = false;
    })

    this.userScopeService.getUserScope().subscribe(response => {
      this.currentScope = response
      if (this.currentScope)
        for (let scope of this.currentScope)
          this.responsableStatus[scope.responsableViewed.id] = this.isInScope(scope.responsableViewed);
      this.isLoading = false;
    });
  }

  tiersExistsInAllTiers(tiers: Tiers) {
    if (tiers && this.allTiers)
      for (let tier of this.allTiers)
        if (tier.id == tiers.id)
          return true;
    return false;
  }

  getTiersLabel(tiers: Tiers): string {
    if (tiers)
      return tiers.denomination ? tiers.denomination : (tiers.firstname + " " + tiers.lastname);
    return "";
  }

  isInScope(responsable: Responsable) {
    if (this.currentScope && responsable)
      for (let scope of this.currentScope) {
        if (scope.responsableViewed.id == responsable.id)
          return true;
      }
    return false;
  }

  toggleScope(responsable: Responsable, selected: boolean) {
    if (this.currentUser)
      if (selected) {
        if (!this.isInScope(responsable) && this.currentScope)
          this.currentScope.push({ responsable: this.currentUser, responsableViewed: responsable } as UserScope)
      } else {
        if (this.currentScope)
          for (let i = 0; i < this.currentScope.length; i++) {
            const scope = this.currentScope[i];
            if (scope.responsableViewed.id == responsable.id) {
              this.currentScope.splice(i, 1);
              break;
            }
          }
      }
  }

  selectAll() {
    if (this.potentialResponsables)
      for (let responsable of this.potentialResponsables)
        this.toggleScope(responsable, true);
  }

  unselectAll() {
    if (this.currentScope)
      this.currentScope = [];
  }

  saveScope() {
    let scopeResponsables = [] as Array<Responsable>;
    if (this.currentScope)
      for (let scope of this.currentScope)
        scopeResponsables.push(scope.responsableViewed);
    this.userScopeService.addToUsersScope(scopeResponsables).subscribe(response => {
      this.initCurrentScope();
    });
  }

}
