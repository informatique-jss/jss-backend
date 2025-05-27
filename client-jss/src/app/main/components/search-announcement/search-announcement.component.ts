import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Subscription } from 'rxjs';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { Announcement } from '../../model/Announcement';
import { AnnouncementService } from '../../services/announcement.service';
import { GenericInputComponent } from "../generic-input/generic-input.component";

@Component({
  selector: 'search-announcement',
  templateUrl: './search-announcement.component.html',
  styleUrls: ['./search-announcement.component.css'],
  imports: [SHARED_IMPORTS, GenericInputComponent],
  standalone: true
})
export class SearchAnnouncementComponent implements OnInit {
  debounce: any;
  isLoading: boolean = false;
  searchObservableRef: Subscription | undefined;
  page: number = 0;
  pageSize: number = 10;
  announcements: Announcement[] = [];
  searchResults: Announcement[] | undefined;
  displayLoadMoreButton: boolean = true;
  searchText: string = "";
  searchAnnouncementForm!: FormGroup;
  isClickedOnce: boolean = false;

  constructor(
    private appService: AppService,
    private announcementService: AnnouncementService,
    private formBuilder: FormBuilder
  ) { }


  ngOnInit() {
    this.searchAnnouncementForm = this.formBuilder.group({});
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
    this.announcementService.getTopAnnouncementSearch(this.page, this.pageSize, this.searchText).subscribe(response => {
      if (response && response.content && response.content.length > 0 && (!this.searchText || this.searchText.length <= 2))
        this.announcements.push(...response.content);
      else if (response && response.content && response.content.length > 0 && this.searchText && this.searchText.length > 2) {
        if (!this.searchResults)
          this.searchResults = [];
        this.searchResults.push(...response.content);
      }
      else
        this.displayLoadMoreButton = false;
    });
  }

  clearSearch() {
    this.searchText = '';
    this.searchResults = [];
    this.isLoading = false;
    this.displayLoadMoreButton = true;
    this.isClickedOnce = false;
  }

  searchForAnnouncement() {
    if (this.searchText && this.searchText.length > 2) {
      this.isLoading = true;
      this.isClickedOnce = true;
      clearTimeout(this.debounce);
      this.searchResults = [];
      this.page = 0;
      this.debounce = setTimeout(() => {
        this.searchAnnouncement();
      }, 500);
    }
  }

  searchAnnouncement() {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();

    this.isLoading = true;
    if (this.searchText && this.searchText.length > 2)
      this.searchObservableRef = this.announcementService.getTopAnnouncementSearch(this.page, this.pageSize, this.searchText).subscribe(response => {
        if (response && response.content && response.content.length > 0) {
          if (!this.searchResults)
            this.searchResults = [];
          this.searchResults.push(...response.content);
        }
        if (!response || response.content.length == 0)
          this.displayLoadMoreButton = false;
        this.isLoading = false;
      })
  }
}
