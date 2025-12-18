import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgIconComponent } from '@ng-icons/core';
import { displayInTeams } from '../../../../libs/MailHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { UiCardComponent } from '../../../../libs/ui-card/ui-card.component';
import { KpiWidgetComponent } from '../../../crm/components/kpi-widget/kpi-widget.component';
import { KpiCrm } from '../../../crm/model/KpiCrm';
import { KpiCrmSearchModel } from '../../../crm/model/KpiCrmSearchModel';
import { KpiCrmService } from '../../../crm/services/kpi.crm.service';
import { PageTitleComponent } from "../../../main/components/page-title/page-title.component";
import { AvatarComponent } from '../../../miscellaneous/components/avatar/avatar.component';
import { Country } from '../../../profile/model/Country';
import { EmployeeService } from '../../../profile/services/employee.service';
import { ResponsableDto } from '../../model/ResponsableDto';
import { ResponsableService } from '../../services/responsable.service';
import { ResponsableHeaderComponent } from '../responsable-header/responsable-header.component';


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
  selector: 'app-responsable',
  templateUrl: './responsable.component.html',
  styleUrls: ['./responsable.component.css'],
  standalone: true,
  imports: [...SHARED_IMPORTS,
    ResponsableHeaderComponent,
    NgIconComponent,
    UiCardComponent,
    AvatarComponent,
    KpiWidgetComponent,
    PageTitleComponent]
})
export class ResponsableComponent implements OnInit {

  breadcrumbPaths: { label: string; route: string; }[] = [];

  responsableId: number | undefined;
  responsable: ResponsableDto | undefined;

  responsableKpis: KpiCrm[] = [];

  searchModel: KpiCrmSearchModel = {
    endDateKpis: new Date(),
    startDateKpis: this.getPreviousYear(new Date(), 1),
    salesEmployeeId: undefined,
    kpiCrmKey: '',
    tiersIds: [],
    responsableIds: [],
    isAllTiers: false,
    kpiScale: 'ANNUALLY'
  } as KpiCrmSearchModel;

  countryFrance: Country | undefined;

  constructor(private activeRoute: ActivatedRoute,
    private responsableService: ResponsableService,
    private employeeService: EmployeeService,
    private kpiCrmService: KpiCrmService,
  ) { }

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

  ngOnInit() {
    if (this.activeRoute.snapshot.params['id'])
      this.responsableId = this.activeRoute.snapshot.params['id'];

    if (this.responsableId) {
      this.fetchKpisForResponsable();
      this.responsableService.getResponsable(this.responsableId).subscribe(response => {
        this.responsable = response;
        this.breadcrumbPaths.push({ label: "Détail du tiers", route: "/tiers/view/" + this.responsable.tiersId })
        this.breadcrumbPaths.push({ label: "Détail du responsable", route: "tiers/view/" + this.responsable.tiersId + "/responsable/" + this.responsableId });
        this.searchModel.responsableIds.push(this.responsable.id);
      })
    }
  }

  fetchKpisForResponsable() {
    this.kpiCrmService.getKpiCrm().subscribe(kpisCrm => {
      for (let kpi of kpisCrm) {
        if (kpi.isToDisplayTiersMainPage) {
          this.responsableKpis.push(kpi);
        }
        if (this.responsableKpis.length > 4)
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

  getPreviousYear(date: Date, offsetYear: number): Date {
    date = new Date(date);
    const year = date.getFullYear() - offsetYear;
    const month = date.getMonth();
    const day = date.getDate();

    const previousYearDate = new Date(year, month, day);

    if (previousYearDate.getMonth() !== month) {
      return new Date(year, month + 1, 0);
    }

    return previousYearDate;
  }
}
