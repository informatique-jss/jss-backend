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
  connectedDropLists: string[] = [];
  debounce: any;
  statusSelected: U[] = [];
  allEntities: T[] = [];
  activeDraggedStatusId: number | null = null;
  panelOpen: boolean = false;
  abstract filterText: string;
  selectedEntity: T | null = null;
  possibleEntityStatus: U[] | undefined;
  numberOfEntitiesByStatus: number[] = [];
  computeAggregatedStatus: boolean = false;

  applyFilter(isOnlyFilterText = false) {
    if (this.swimlanes)
      for (let swimlane of this.swimlanes) {
        swimlane.status = [];
        swimlane.totalItems = 0;
      }

    this.connectedDropLists = this.statusSelected.map((status) => 'list-' + status.id);

    // Set bookmark
    this.numberOfEntitiesByStatus = [];
    if (this.allEntities && this.allEntities.length > 0)
      this.saveUserPreferencesOnApplyFilter();
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

  abstract saveUserPreferencesOnApplyFilter(): void;
  abstract findEntities(): Observable<T[]>;
  abstract getEntityStatus(entity: T): U;
  abstract fetchEntityAndOpenPanel(task: T, refreshColumn: boolean, openPanel: boolean): void;

  filterCard() {
    if (this.allEntities && this.selectedSwimlaneType) {
      for (let order of this.allEntities) {
        let orderValue = this.selectedSwimlaneType.fieldValueFunction ? this.selectedSwimlaneType.fieldValueFunction(order) : getObjectPropertybyString(order, this.selectedSwimlaneType.fieldName);
        let swimlane = this.findSwimlaneByValue(this.swimlanes, orderValue);
        if (!swimlane) {
          swimlane = { rawLabel: orderValue, label: (this.selectedSwimlaneType.valueFonction ? this.selectedSwimlaneType.valueFonction(order) : orderValue), isCollapsed: false, status: copyObjectList(this.statusSelected, false), totalItems: 0 } as Swimlane<U>;
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
                if (!this.numberOfEntitiesByStatus[statu.id])
                  this.numberOfEntitiesByStatus[statu.id] = 0;
                this.numberOfEntitiesByStatus[statu.id]++;
              }
            }
          }
        }
      }
    }

    if (this.computeAggregatedStatus) {
      for (let swimlane of this.swimlanes)
        swimlane.aggregatedStatus = this.getAggregatedSwimlane(swimlane);
    }
    return this.swimlanes.sort((a, b) => a.label.localeCompare(b.label));
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
        existing.entities = [...(existing.entities ?? []), ...currentEntities];
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

  onDragStarted(statusId: number) {
    this.activeDraggedStatusId = statusId;
  }

  onDragEnded() {
    this.activeDraggedStatusId = null;
  }

  drop(event: CdkDragDrop<T[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      const fromId = parseInt(event.previousContainer.id.split('-')[1], 10);
      const toId = parseInt(event.container.id.split('-')[1], 10);

      const fromStatus = this.possibleEntityStatus?.find(status => status.id === fromId);
      const isAllowed = fromStatus?.successors?.some(successor => successor.id === toId) || fromStatus?.predecessors?.some(predecessor => predecessor.id === toId);

      if (!isAllowed) return;

      const toStatus = this.possibleEntityStatus?.find(status => status.id === toId);
      if (toStatus && event.previousContainer.data && event.previousContainer.data.length > 0)
        this.changeEntityStatus(event.previousContainer.data[event.previousIndex] as T, toStatus);
    }
  }

  isValidDropTarget(targetId: number): boolean {
    const fromStatus = this.possibleEntityStatus?.find(status => status.id === this.activeDraggedStatusId);
    return !!fromStatus?.successors?.some(successor => successor.id === targetId) || !!fromStatus?.predecessors?.some(predecessor => predecessor.id === targetId);
  }

  abstract changeEntityStatus(entity: T, toStatus: U): void;

  getCompleteStatus(status: U) {
    if (this.possibleEntityStatus)
      return this.possibleEntityStatus.filter(s => s.code == status.code)[0];
    return null;
  }
}
