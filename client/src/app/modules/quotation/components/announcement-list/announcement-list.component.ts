import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from '../../../../services/app.service';
import { Announcement } from '../../model/Announcement';
import { AnnouncementSearch } from '../../model/AnnouncementSearch';
import { Confrere } from '../../model/Confrere';
import { AnnouncementService } from '../../services/announcement.service';
import { CustomerOrderService } from '../../services/customer.order.service';

@Component({
  selector: 'announcement-list',
  templateUrl: './announcement-list.component.html',
  styleUrls: ['./announcement-list.component.css']
})
export class AnnouncementListComponent implements OnInit, AfterContentChecked {

  @Input() confrere: Confrere | undefined;
  announcements: Announcement[] = [];
  availableColumns: SortTableColumn[] = [];
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  announcementSearch: AnnouncementSearch = {} as AnnouncementSearch;
  constructor(
    private announcementService: AnnouncementService,
    private changeDetectorRef: ChangeDetectorRef,
    private customerOrderService: CustomerOrderService,
    private appService: AppService,
    private formBuilder: FormBuilder,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.availableColumns = [];
    this.availableColumns.push({ id: "id", fieldName: "id", label: "N° de l'annonce" } as SortTableColumn);
    this.availableColumns.push({ id: "confrere", fieldName: "confrere.label", label: "Confrère" } as SortTableColumn);
    this.availableColumns.push({ id: "publicationDate", fieldName: "publicationDate", label: "Date de publication", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "noticeTypes", fieldName: "noticeTypes", label: "Rubrique(s)", valueFonction: (element: Announcement) => { return element.noticeTypes.map(notice => notice.label).join(" / ") } } as SortTableColumn);
    this.availableColumns.push({ id: "announcementStatus", fieldName: "announcementStatus.label", label: "Statut" } as SortTableColumn);
    this.availableColumns.push({ id: "isPublicationReciptAlreadySent", fieldName: "isPublicationReciptAlreadySent", label: "Attestation de parution envoyée ?", valueFonction: (element: any) => { return element.isPublicationReciptAlreadySent ? "Oui" : "Non" } } as SortTableColumn);
    this.availableColumns.push({ id: "isPublicationFlagAlreadySent", fieldName: "isPublicationFlagAlreadySent", label: "Témoin de parution envoyé ?", valueFonction: (element: any) => { return element.isPublicationFlagAlreadySent ? "Oui" : "Non" } } as SortTableColumn);

    this.tableAction.push({
      actionIcon: "shopping_cart", actionName: "Voir la commande", actionClick: (action: SortTableAction, element: Announcement, event: any) => {
        this.customerOrderService.getCustomerOrderOfAnnouncement(element).subscribe(response => {
          this.appService.openRoute(event, "/order/" + response.id, undefined);
        })
      }, display: true,
    } as SortTableAction);

    this.setColumns();
    this.refreshData();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes && this.confrere)
      this.refreshData();
  }

  refreshData() {
    if (this.confrere) {
      this.announcementSearch.confrere = this.confrere;
      this.announcementSearch.endDate = new Date();
      this.announcementSearch.startDate = new Date();
      this.announcementSearch.startDate.setDate(this.announcementSearch.startDate.getDate() - 30);
      this.searchAnnouncements();
    }
  }

  announcementListForm = this.formBuilder.group({
  });

  setColumns() {
    this.displayedColumns.push(...this.availableColumns);
  }

  searchAnnouncements() {
    if (this.announcementListForm.valid) {
      this.announcementService.getAnnouncements(this.announcementSearch).subscribe(response => {
        this.announcements = response;
      })
    }
  }

}
