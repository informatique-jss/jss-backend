import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { MenuItem } from '../../general/model/MenuItem';

@Injectable({
  providedIn: 'root'
})
export class TabService {
  private selectedTabSource = new Subject<MenuItem>();
  selectedTab$ = this.selectedTabSource.asObservable();

  updateSelectedTab(tab: MenuItem) {
    this.selectedTabSource.next(tab);
  }
}