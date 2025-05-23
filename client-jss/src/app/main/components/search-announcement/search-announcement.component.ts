import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { AppService } from '../../../services/app.service';
import { Announcement } from '../../model/Announcement';
import { AnnouncementService } from '../../services/announcement.service';

@Component({
  selector: 'app-search-announcement',
  templateUrl: './search-announcement.component.html',
  styleUrls: ['./search-announcement.component.css'],
  standalone: false
})
export class SearchAnnouncementComponent implements OnInit {

  page: number = 0;
  announcements: Announcement[] = [];
  searchResults: Announcement[] | undefined;
  displayLoadMoreButton: boolean = true;
  searchText: string = "";

  searchObservableRef: Subscription | undefined;
  debounce: any;
  searchInProgress: boolean = false;

  constructor(
    private appService: AppService,
    private announcementService: AnnouncementService
  ) { }

  ngOnInit() {
    this.fetchNextAnnouncements();
  }

  getNextAnnouncements() {
    this.page++;
    this.fetchNextAnnouncements();
  }

  openAnnouncement(announcement: Announcement, event: any) {
    this.appService.openRoute(event, "announcement/" + announcement.id, undefined);
  }

  fetchNextAnnouncements() {
    if (this.searchText && this.searchText.length > 2) {
      this.globalSearch();
    } else
      this.announcementService.getTopAnnouncement(this.page).subscribe(announcements => {
        if (announcements && announcements.length > 0) {
          this.announcements.push(...announcements);
        } else {
          this.displayLoadMoreButton = false;
        }
      });
  }

  searchForAnnouncement() {
    this.searchInProgress = true;
    clearTimeout(this.debounce);
    this.searchResults = [];
    this.page = 0;
    this.debounce = setTimeout(() => {
      this.globalSearch();
    }, 500);
  }

  globalSearch() {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();

    this.searchInProgress = true;
    if (this.searchText && this.searchText.length > 2)
      this.searchObservableRef = this.announcementService.getTopAnnouncementSearch(this.page, this.searchText, this.searchText, this.searchText).subscribe(response => {
        if (!this.searchResults)
          this.searchResults = [];
        this.searchResults.push(...response);
        this.searchInProgress = false;

        if (!response || response.length == 0)
          this.displayLoadMoreButton = false;
      })
  }
}
