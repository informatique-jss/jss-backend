import { Component, OnInit } from '@angular/core';
import { Observable, Subject, retry, share, switchMap, takeUntil, tap, timer } from 'rxjs';
import { SUPERVISION_REFRESH_INTERVAL } from 'src/app/libs/Constants';
import { Node } from '../../model/Node';
import { NodeService } from '../../services/node.service';

@Component({
  selector: 'monitoring-nodes',
  templateUrl: './monitoring-nodes.component.html',
  styleUrls: ['./monitoring-nodes.component.css']
})
export class MonitoringNodesComponent implements OnInit {

  private stopPolling = new Subject();
  statisticsNodeNotifications: Observable<Node[]> | undefined;
  nodes: Node[] | undefined;

  constructor(
    private nodeService: NodeService
  ) {
    this.statisticsNodeNotifications = timer(1, SUPERVISION_REFRESH_INTERVAL).pipe(
      switchMap(() => this.nodeService.getNodes()),
      retry(),
      tap((value) => {
        if (value)
          this.nodes = value;
      }),
      share(),
      takeUntil(this.stopPolling)
    );
  }

  ngOnInit() {
    if (this.statisticsNodeNotifications)
      this.statisticsNodeNotifications.subscribe();
  }

  ngOnDestroy() {
    this.stopPolling.next(false);
  }

  getCpuLoad(node: Node): number {
    return Math.round(node.cpuLoad * 100);
  }

  getRamLoad(node: Node): number {
    return Math.round((node.totalMemory - node.freeMemory) / node.totalMemory * 100);
  }

  getDiskLoad(node: Node): number {
    return Math.round((node.totalSpace - node.freeSpace) / node.totalSpace * 100);
  }

  trackByHostname(index: any, node: Node) {
    return node.hostname;
  }

  performGc(node: Node) {
    this.nodeService.performGc(node).subscribe();
  }

  stopNode(node: Node) {
    this.nodeService.stopNode(node).subscribe();
  }

  restartNode(node: Node) {
    this.nodeService.restartNode(node).subscribe();
  }

}
