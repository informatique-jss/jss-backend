import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../services/app.service';

@Component({
  selector: 'loading',
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class LoadingComponent implements OnInit {

  display: boolean = false;

  constructor(private appService: AppService) { }

  ngOnInit() {
    this.appService.loadingSpinnerObservable.subscribe(response => {
      this.display = response;
    })
  }
}
