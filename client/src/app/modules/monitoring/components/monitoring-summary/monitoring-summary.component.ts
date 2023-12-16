import { CdkDragEnter, CdkDropList, DragRef, moveItemInArray } from '@angular/cdk/drag-drop';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { BatchSettings } from '../../model/BatchSettings';
import { BatchStatistics } from '../../model/BatchStatictics';
import { BatchSettingsService } from '../../services/batch.settings.service';
import { BatchStatisticsService } from '../../services/batch.statistics.settings.service';

@Component({
  selector: 'monitoring-summary',
  templateUrl: './monitoring-summary.component.html',
  styleUrls: ['./monitoring-summary.component.css']
})
export class MonitoringSummaryComponent implements OnInit {

  @ViewChild(CdkDropList) placeholder!: CdkDropList;

  private target: CdkDropList | null = null;
  private targetIndex!: number;
  private source: CdkDropList | null = null;
  private sourceIndex!: number;
  private dragRef: DragRef | null = null;
  items: Array<BatchSettings> = [];
  settings: BatchSettings[] | undefined;
  statistics: BatchStatistics[] = [];

  constructor(private appService: AppService,
    private batchSettingsService: BatchSettingsService,
    private batchStatisticsService: BatchStatisticsService,
    private habilitationsService: HabilitationsService
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Supervision");
    this.batchSettingsService.getBatchSettings().subscribe(response => {
      this.settings = response;
      if (this.settings)
        for (let setting of this.settings)
          this.items.push(setting);
    })
    this.batchStatisticsService.getBatchStatistics().subscribe(response => {
      this.statistics = [];
      if (response)
        for (let stat of response)
          this.statistics[stat.idBatchSettings] = stat;
    })
  }

  isItWarn(statistics: BatchStatistics): boolean {
    if (statistics) {
      if (statistics.standardMeanTime < statistics.currentMeanTime * 0.75)
        return true;
    }
    return false;
  }

  getTooltipStatistics(statistics: BatchStatistics): string {
    if (statistics) {
      return "Erreurs : " + statistics.error + " / Acquités : " + statistics.acknowledge + " / En attente : " + statistics.waiting + " / En cours : " + statistics.running + " / Terminés avec succès : " + statistics.success + " / Temps moyen aujourd'hui : " + Math.round(statistics.currentMeanTime * 10) / 10 + "s / Temps moyen : " + Math.round(statistics.standardMeanTime * 10) / 10 + "s";
    }
    return "";
  }

  canDisplayExtendentMonitoring() {
    return this.habilitationsService.canDisplayExtendentMonitoring();
  }

  /* Drag & drop functions */

  boxWidth = '400px';
  boxHeight = '300px';

  ngAfterViewInit() {
    const placeholderElement = this.placeholder.element.nativeElement;

    placeholderElement.style.display = 'none';
    placeholderElement.parentNode!.removeChild(placeholderElement);
  }

  onDropListDropped() {
    if (!this.target) {
      return;
    }

    const placeholderElement: HTMLElement =
      this.placeholder.element.nativeElement;
    const placeholderParentElement: HTMLElement =
      placeholderElement.parentElement!;

    placeholderElement.style.display = 'none';

    placeholderParentElement.removeChild(placeholderElement);
    placeholderParentElement.appendChild(placeholderElement);
    placeholderParentElement.insertBefore(
      this.source!.element.nativeElement,
      placeholderParentElement.children[this.sourceIndex]
    );

    if (this.placeholder._dropListRef.isDragging()) {
      this.placeholder._dropListRef.exit(this.dragRef!);
    }

    this.target = null;
    this.source = null;
    this.dragRef = null;

    if (this.sourceIndex !== this.targetIndex) {
      moveItemInArray(this.items, this.sourceIndex, this.targetIndex);
    }
  }

  onDropListEntered({ item, container }: CdkDragEnter) {
    if (container == this.placeholder) {
      return;
    }

    const placeholderElement: HTMLElement =
      this.placeholder.element.nativeElement;
    const sourceElement: HTMLElement = item.dropContainer.element.nativeElement;
    const dropElement: HTMLElement = container.element.nativeElement;
    const dragIndex: number = Array.prototype.indexOf.call(
      dropElement.parentElement!.children,
      this.source ? placeholderElement : sourceElement
    );
    const dropIndex: number = Array.prototype.indexOf.call(
      dropElement.parentElement!.children,
      dropElement
    );

    if (!this.source) {
      this.sourceIndex = dragIndex;
      this.source = item.dropContainer;

      placeholderElement.style.width = this.boxWidth + 'px';
      placeholderElement.style.height = this.boxHeight + 40 + 'px';

      sourceElement.parentElement!.removeChild(sourceElement);
    }

    this.targetIndex = dropIndex;
    this.target = container;
    this.dragRef = item._dragRef;

    placeholderElement.style.display = '';

    dropElement.parentElement!.insertBefore(
      placeholderElement,
      dropIndex > dragIndex ? dropElement.nextSibling : dropElement
    );

    this.placeholder._dropListRef.enter(
      item._dragRef,
      item.element.nativeElement.offsetLeft,
      item.element.nativeElement.offsetTop
    );
  }

}
