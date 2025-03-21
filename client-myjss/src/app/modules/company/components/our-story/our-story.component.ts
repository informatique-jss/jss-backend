import { AfterViewInit, Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';
import { AppService } from '../../../../libs/app.service';
import { MenuItem } from '../../../general/model/MenuItem';

@Component({
  selector: 'our-story',
  templateUrl: './our-story.component.html',
  styleUrls: ['./our-story.component.css'],
})
export class OurStoryComponent implements OnInit, AfterViewInit {

  companyItems: MenuItem[] = this.appService.getAllCompanyMenuItems();

  constructor(
    private appService: AppService,
  ) { }

  ngOnInit(): void { }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }
}
