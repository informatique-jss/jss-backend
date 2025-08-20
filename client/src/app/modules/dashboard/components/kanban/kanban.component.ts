import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Directive } from '@angular/core';
import { Observable } from 'rxjs';
import { getObjectPropertybyString } from 'src/app/libs/FormatHelper';
import { copyObjectList } from 'src/app/libs/GenericHelper';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
import { Swimlane } from '../../model/Swimlane';
import { SwimlaneType } from '../../model/SwimlaneType';

@Directive()
export abstract class KanbanComponent<T, U extends IWorkflowElement<T>> {

  swimlaneTypes: SwimlaneType<T>[] = [] as SwimlaneType<T>[];
  selectedSwimlaneType: SwimlaneType<T> | undefined;
  swimlanes: Swimlane<U>[] = [] as Swimlane<U>[];
  debounce: any;
  statusSelected: U[] = [];
  allEntities: T[] = [];
  activeDraggedStatusId: number | null = null;
  activeDraggedEntity: T | null = null;
  panelOpen: boolean = false;
  abstract filterText: string;
  selectedEntity: T | null = null;
  possibleEntityStatus: U[] | undefined;
  numberOfEntitiesByStatus: number[] = [];
  computeAggregatedStatus: boolean = false;
  statusId: string[] = [];

  applyFilter(isOnlyFilterText = false) {

    if (this.swimlanes)
      for (let swimlane of this.swimlanes) {
        swimlane.status = [];
        swimlane.totalItems = 0;
      }


    // Set bookmark
    this.numberOfEntitiesByStatus = [];
    if (!isOnlyFilterText) {
      if (this.statusSelected && this.statusSelected.length > 0) {
        this.findEntities().subscribe(response => {
          this.allEntities = response;
          this.filterCard();
        });
      }
    } else {
      this.filterCard();
    }

  }

  getConnectedDropLists(swimlane: Swimlane<U>) {
    if (this.computeAggregatedStatus)
      return swimlane.aggregatedStatus.map(s => 'list-' + this.statusId.indexOf(s.label + '-' + swimlane.id));
    else
      return swimlane.status.map(s => 'list-' + this.statusId.indexOf(s.label + '-' + swimlane.id));
  }

  abstract saveUserPreferencesOnApplyFilter(): void;
  abstract findEntities(): Observable<T[]>;
  abstract getEntityStatus(entity: T): U;
  abstract fetchEntityAndOpenPanel(task: T, refreshColumn: boolean, openPanel: boolean): void;

  filterCard() {
    let i = 0;
    this.numberOfEntitiesByStatus = [];
    if (this.allEntities && this.selectedSwimlaneType) {
      for (let order of this.allEntities) {
        let orderValue = this.selectedSwimlaneType.fieldValueFunction ? this.selectedSwimlaneType.fieldValueFunction(order) : getObjectPropertybyString(order, this.selectedSwimlaneType.fieldName);
        let swimlane = this.findSwimlaneByValue(this.swimlanes, orderValue);
        if (!swimlane) {
          swimlane = { rawLabel: orderValue, label: (this.selectedSwimlaneType.valueFonction ? this.selectedSwimlaneType.valueFonction(order) : orderValue), isCollapsed: false, status: copyObjectList(this.statusSelected, false), totalItems: 0 } as Swimlane<U>;
          swimlane.id = i;
          i++;
          this.swimlanes.push(swimlane);
        }
        if (swimlane.status) {
          for (let statu of swimlane.status) {
            if (statu.id == this.getEntityStatus(order).id && this.shouldDisplayCard(order)) {
              if (!statu.entities)
                statu.entities = [];
              statu.entities.push(order as any);
              swimlane.totalItems++;

              if (statu.id) {
                if (!this.numberOfEntitiesByStatus[statu.label as any])
                  this.numberOfEntitiesByStatus[statu.label as any] = 0;
                this.numberOfEntitiesByStatus[statu.label as any]++;
              }
            }
          }
        }
      }
    }

    if (this.computeAggregatedStatus) {
      this.numberOfEntitiesByStatus = [];
      for (let swimlane of this.swimlanes) {
        swimlane.aggregatedStatus = this.getAggregatedSwimlane(swimlane);
        swimlane.totalItems = 0;
        if (swimlane.aggregatedStatus)
          for (let status of swimlane.aggregatedStatus) {
            swimlane.totalItems += status.entities.length;

            if (status.id) {
              if (!this.numberOfEntitiesByStatus[status.label as any])
                this.numberOfEntitiesByStatus[status.label as any] = status.entities.length;
              else
                this.numberOfEntitiesByStatus[status.label as any] += status.entities.length;
            }
          }
      }
    }

    this.statusId = [];
    if (this.possibleEntityStatus)
      for (let swimlane of this.swimlanes) {
        for (let status of this.deduplicateArrayByLabel(this.possibleEntityStatus)) {
          this.statusId.push(status.label + '-' + swimlane.id);
        }
      }

    this.saveUserPreferencesOnApplyFilter();
    return this.swimlanes.sort((a, b) => a.label.localeCompare(b.label));
  }

  getNumberOfEntitiesByStatus(status: U) {
    return this.numberOfEntitiesByStatus[status.label as any];
  }

  findSwimlaneByValue(swimlanes: Swimlane<U>[], value: string): Swimlane<U> | undefined {
    if (swimlanes) {
      for (let swimlane of swimlanes)
        if (swimlane.rawLabel == value) {
          if (!swimlane.status || swimlane.status.length == 0)
            swimlane.status = copyObjectList(this.statusSelected, false);
          return swimlane;
        }
    }
    return undefined;
  }

  getAggregatedSwimlane(swimlane: Swimlane<U>): U[] {
    const grouped = new Map<string, U>();

    for (const status of swimlane.status) {
      const key = status.label;
      const currentEntities = status.entities ?? [];

      if (grouped.has(key)) {
        const existing = grouped.get(key)!;
        const existingEntities = existing.entities ?? [];

        // Remove duplicate, by id
        const mergedEntities = [...existingEntities, ...currentEntities];
        const uniqueEntities = new Map<number | string, typeof mergedEntities[0]>();
        for (const e of mergedEntities) {
          const entityId = (e as any).id;
          if (entityId !== undefined) {
            uniqueEntities.set(entityId, e);
          }
        }

        existing.entities = Array.from(uniqueEntities.values());
      } else {
        grouped.set(key, {
          ...status,
          entities: [...currentEntities],
        });
      }
    }
    return Array.from(grouped.values());
  }

  deduplicateArrayById(array: U[]) {
    let seen = new Set<number>();
    var deduplicated = array.filter(item => {
      if (seen.has(item.id!)) {
        return false;
      }
      seen.add(item.id!);
      return true;
    });
    return deduplicated;
  }

  deduplicateArrayByLabel(array: U[]) {
    let seen = new Set<string>();
    var deduplicated = array.filter(item => {
      if (seen.has(item.label!)) {
        return false;
      }
      seen.add(item.label!);
      return true;
    });
    return deduplicated;
  }

  expandAll() {
    if (this.swimlanes)
      for (let swimlane of this.swimlanes)
        swimlane.isCollapsed = false;
  }

  collapseAll() {
    if (this.swimlanes)
      for (let swimlane of this.swimlanes)
        swimlane.isCollapsed = true;
  }

  shouldDisplayCard(task: T): boolean {
    if (!this.filterText || !this.filterText.trim()) return true;
    const normalizedFilter = this.normalize(this.filterText.trim().toLowerCase());
    return this.normalize(JSON.stringify(task).toLowerCase()).includes(normalizedFilter);
  }

  normalize(str: string): string {
    return str.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '');
  }



  closePanel() {
    this.panelOpen = false;
    this.selectedEntity = null;
  }

  // Drag & drop management

  onDragStarted(statusId: number, entity: T) {
    this.activeDraggedStatusId = statusId;
    this.activeDraggedEntity = entity;
  }

  onDragEnded() {
    this.activeDraggedStatusId = null;
  }

  drop(event: CdkDragDrop<T[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      const toLabel = this.statusId[parseInt(event.container.id.split('-')[1])].split('-')[0];

      const fromStatus = this.getEntityStatus(this.activeDraggedEntity!);
      const isAllowed = fromStatus?.successors?.some(successor => successor.label == toLabel) || fromStatus?.predecessors?.some(predecessor => predecessor.label == toLabel);

      if (!isAllowed) return;

      let toStatus = fromStatus?.successors?.find(status => status.label === toLabel);
      if (!toStatus)
        toStatus = fromStatus?.predecessors?.find(status => status.label === toLabel);

      if (toStatus && event.previousContainer.data && event.previousContainer.data.length > 0)
        this.changeEntityStatus(event.previousContainer.data[event.previousIndex] as T, toStatus as U);
    }
  }

  isValidDropTarget(targetLabel: string): boolean {
    const fromStatus = this.getFromStatus();
    return !!fromStatus?.successors?.some(successor => successor.label === targetLabel) || !!fromStatus?.predecessors?.some(predecessor => predecessor.label === targetLabel);
  }

  getFromStatus() {
    if (this.activeDraggedEntity != null && this.activeDraggedStatusId != null) {
      return this.possibleEntityStatus?.find(status => status.id === this.getEntityStatus(this.activeDraggedEntity!).id);
    }
    return null;
  }

  abstract changeEntityStatus(entity: T, toStatus: U): void;

  getCompleteStatus(status: U) {
    if (this.possibleEntityStatus)
      return this.possibleEntityStatus.filter(s => s.code == status.code)[0];
    return null;
  }
}
