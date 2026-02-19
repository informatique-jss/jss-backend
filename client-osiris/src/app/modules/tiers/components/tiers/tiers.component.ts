import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgIconComponent } from "@ng-icons/core";
import { getPreviousYear } from '../../../../libs/DateHelper';
import { callNumber, displayInTeams } from '../../../../libs/MailHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { UiCardComponent } from '../../../../libs/ui-card/ui-card.component';
import { KpiWidgetComponent } from '../../../crm/components/kpi-widget/kpi-widget.component';
import { KpiCrm } from '../../../crm/model/KpiCrm';
import { KpiCrmSearchModel } from '../../../crm/model/KpiCrmSearchModel';
import { KpiCrmService } from '../../../crm/services/kpi.crm.service';
import { PageTitleComponent } from "../../../main/components/page-title/page-title.component";
import { ConstantService } from '../../../main/services/constant.service';
import { AvatarComponent } from '../../../miscellaneous/components/avatar/avatar.component';
import { Country } from '../../../profile/model/Country';
import { EmployeeService } from '../../../profile/services/employee.service';
import { ResponsableDto } from '../../model/ResponsableDto';
import { TiersDto } from '../../model/TiersDto';
import { ResponsableService } from '../../services/responsable.service';
import { TiersService } from '../../services/tiers.service';
import { TiersHeaderComponent } from "../tiers-header/tiers-header.component";

// TODO : delete when using real notifications for timeline
export type TimelineType = {
  id: number;
  time?: string;
  title: string;
  description: string;
  name: string;
  variant?: string;
  avatar?: string;
  icon?: string;
}


@Component({
  selector: 'app-tiers',
  templateUrl: './tiers.component.html',
  styleUrls: ['./tiers.component.css'],
  standalone: true,
  imports: [...SHARED_IMPORTS,
    TiersHeaderComponent,
    NgIconComponent,
    UiCardComponent,
    AvatarComponent,
    KpiWidgetComponent, PageTitleComponent]
})
export class TiersComponent implements OnInit {

  breadcrumbPaths: { label: string; route: string; }[] = [];

  tiersId: number | undefined;
  tiers: TiersDto | undefined;

  tiersResponsables: ResponsableDto[] = [];

  tiersResponsablesToDisplay: ResponsableDto[] = [];

  tiersKpis: KpiCrm[] = [];

  inactiveResponsablesIcon = "tablerUserOff";
  isShowInactiveResponsable: boolean = true;
  inactiveResponsablesTooltip: string = "Afficher les responsables inactifs";

  searchModel: KpiCrmSearchModel = {
    endDateKpis: new Date(),
    startDateKpis: getPreviousYear(new Date(), 1),
    salesEmployeeId: undefined,
    kpiCrmKey: '',
    tiersIds: [],
    responsableIds: [],
    isAllTiers: false,
    kpiScale: 'ANNUALLY'
  } as KpiCrmSearchModel;

  countryFrance: Country | undefined;

  // TODO : delete when using real notifications :
  iconTimelineData: TimelineType[] = [
    {
      id: 1,
      time: '5 mins ago',
      title: 'Bug Fix Deployed',
      description: 'Resolved a critical login issue affecting mobile users.',
      name: 'Marcus Bell',
      variant: 'primary',
      icon: 'tablerBug'
    },
    {
      id: 2,
      time: 'Today, 9:00 AM',
      title: 'Marketing Strategy Call',
      description: 'Outlined Q2 goals and content plan for the product launch campaign.',
      name: 'Emily Davis',
      variant: 'danger',
      icon: 'tablerPhoneCall'
    },
    {
      id: 3,
      time: 'Yesterday, 4:45 PM',
      title: 'Feature Planning Session',
      description: 'Prioritized new features for the upcoming release based on user feedback.',
      name: 'Daniel Kim',
      variant: 'warning',
      icon: 'tablerLayersSubtract'
    },
    {
      id: 4,
      time: 'Tuesday, 11:30 AM',
      title: 'UI Enhancements Pushed',
      description: 'Improved dashboard responsiveness and added dark mode support.',
      name: 'Sofia Martinez',
      variant: 'info',
      icon: 'tablerLayoutDashboard'
    },
    {
      id: 5,
      time: 'Last Thursday, 2:20 PM',
      title: 'Security Audit Completed',
      description: 'Reviewed backend API endpoints and applied new encryption standards.',
      name: 'Jonathan Lee',
      variant: 'purple',
      icon: 'tablerShieldLock'
    }
  ];


  constructor(
    private activeRoute: ActivatedRoute,
    private tiersService: TiersService,
    private responsableService: ResponsableService,
    private constantService: ConstantService,
    private employeeService: EmployeeService,
    private kpiCrmService: KpiCrmService,
  ) { }

  ngOnInit() {
    this.countryFrance = this.constantService.getCountryFrance();
    this.breadcrumbPaths.push({ label: "Liste des tiers", route: "/tiers/" })

    if (this.activeRoute.snapshot.params['id']) {
      this.tiersId = this.activeRoute.snapshot.params['id'];
      this.breadcrumbPaths.push({ label: "Détail du tiers", route: "/tiers/view/" + this.tiersId })
    }

    if (this.tiersId) {
      this.fetchKpisForTiers();
      this.searchModel.tiersIds.push(this.tiersId);
      this.tiersService.getTiersById(this.tiersId).subscribe(response => {
        this.tiers = response;
        if (this.tiers) {
          if (this.tiers.id)
            this.responsableService.getResponsablesByTiers(this.tiers.id).subscribe(response => {
              this.tiersResponsables = response;
              this.showInactiveResponsables();
            });
        }
      });
    }
  }

  fetchKpisForTiers() {
    this.kpiCrmService.getKpiCrm().subscribe(kpisCrm => {
      for (let kpi of kpisCrm) {
        if (kpi.isToDisplayTiersMainPage) {
          this.tiersKpis.push(kpi);
        }
        if (this.tiersKpis.length > 4)
          break;
      }
    })
  }

  openTeamsConversation(employee: string) {
    this.employeeService.getEmployeeByName(employee).subscribe(res => {
      if (res) {
        displayInTeams(res);
      }
    });
  }

  showInactiveResponsables() {
    this.isShowInactiveResponsable = !this.isShowInactiveResponsable;
    if (this.isShowInactiveResponsable) {
      this.inactiveResponsablesIcon = "tablerUserCancel";
      this.inactiveResponsablesTooltip = "Masquer les responsables inactifs";
      this.tiersResponsablesToDisplay = this.tiersResponsables
    } else {
      this.inactiveResponsablesIcon = "tablerUserOff";
      this.inactiveResponsablesTooltip = "Afficher les responsables inactifs";
      this.tiersResponsablesToDisplay = this.tiersResponsables.filter(resp => resp.isActive == true);
    }
  }

  callPhoneNumber(phone: string) {
    callNumber(phone);
  }
}
