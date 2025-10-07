import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { SHARED_IMPORTS } from '../../libs/SharedImports';
import { AppService } from '../main/services/app.service';
import { GenericResponsablesKpiComponent } from './components/generic-responsables-kpi/generic-responsables-kpi.component';
import { SidebarComponent } from './components/right-sidebar/sidebar.component';
import { TiersSummaryComponent } from "./components/tiers-summary/tiers-summary.component";
import { TiersService } from './services/tiers.service';

@Component({
  selector: 'tiers-responsables',
  imports: [SHARED_IMPORTS,
    NgbNavModule,
    GenericResponsablesKpiComponent,
    SidebarComponent, TiersSummaryComponent],
  standalone: true,
  templateUrl: './tiers-responsables.component.html',
  styleUrls: ['./tiers-responsables.component.scss'],
})
export class TiersResponsablesComponent implements OnInit {
  pageCode: string | undefined;
  constructor(
    private activatedRoute: ActivatedRoute,
    private appService: AppService,
    private tiersService: TiersService,
  ) { }

  ngOnInit() {
    let idTiersSelected = this.activatedRoute.snapshot.params['idTiers'];
    this.pageCode = this.activatedRoute.snapshot.params['pageCode'];
    if (!idTiersSelected) {
      // TODO : faire la page de selection du Tiers (tableau avec liste des tiers filtrable et ligne cliquable pour aller sur le tiers en question)
      this.appService.openRoute(null, "tiers-selection", null);
    } else {
      this.tiersService.selectTiers(idTiersSelected);
    }

  }

}
