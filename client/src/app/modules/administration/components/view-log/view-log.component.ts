import { AfterContentChecked, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AppService } from '../../../../services/app.service';
import { OsirisLog } from '../../model/OsirisLog';
import { OsirisLogService } from '../../services/osiris.log.service';

@Component({
  selector: 'view-log',
  templateUrl: './view-log.component.html',
  styleUrls: ['./view-log.component.css']
})
export class ViewLogComponent implements OnInit, AfterContentChecked {

  idLog: number | undefined;
  log: OsirisLog = {} as OsirisLog;
  logDateTime: string | undefined;

  logForm = this.formBuilder.group({});

  constructor(
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private osirisLogService: OsirisLogService,
    private changeDetectorRef: ChangeDetectorRef,
    private appService: AppService
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.appService.changeHeaderTitle("Logs");

    this.idLog = this.activatedRoute.snapshot.params.id;
    if (this.idLog)
      this.osirisLogService.getLog(this.idLog).subscribe(response => {
        this.log = response;
        this.logDateTime = new Date(this.log.createdDateTime).toISOString().substring(0, 16);
      });
  }

}
