import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { EChartsOption } from 'echarts';
import { IWorkflowElement } from '../../model/IWorkflowElement';

@Component({
  selector: 'app-workflow-dialog',
  templateUrl: './workflow-dialog.component.html',
  styleUrls: ['./workflow-dialog.component.css']
})
export class WorkflowDialogComponent implements OnInit {

  title: string = "Worklow";
  workflowElements: IWorkflowElement[] | undefined;
  fixedWorkflowElement: IWorkflowElement | undefined;
  activeWorkflowElement: IWorkflowElement | undefined;
  excludedWorkflowElement: IWorkflowElement | undefined;

  chartOption: EChartsOption = {};

  defaultColor: string = "#1C3D58";
  activeColor: string = "#c0392b";

  constructor(public dialogRef: MatDialogRef<WorkflowDialogComponent>,) { }

  ngOnInit() {
  }

  onClose(): void {
    this.dialogRef.close(false);
  }

  onChartInit($event: any) {
    if (this.workflowElements) {

      let statusData = [];
      let statusLink = [];

      for (let status of this.workflowElements) {
        if (!this.excludedWorkflowElement || status.label != this.excludedWorkflowElement.label) {
          if (this.fixedWorkflowElement && status.label == this.fixedWorkflowElement.label)
            statusData.push({
              name: status.label,
              fixed: true,
              x: 100,
              y: 100,
              itemStyle: { color: (this.activeWorkflowElement && this.activeWorkflowElement.label == status.label) ? this.activeColor : this.defaultColor }
            });
          else
            statusData.push({
              name: status.label,
              itemStyle: { color: (this.activeWorkflowElement && this.activeWorkflowElement.label == status.label) ? this.activeColor : this.defaultColor }
            });

          if (status.successors)
            for (let successor of status.successors)
              statusLink.push({
                source: status.label,
                target: successor.label
              });

          if (status.predecessors)
            for (let predecessor of status.predecessors)
              statusLink.push({
                source: status.label,
                target: predecessor.label
              });
        }
      }
      this.chartOption = {
        series: [
          {
            type: 'graph',
            layout: 'force',
            zoom: 0.7,
            force: {
              repulsion: 7000
            },
            symbolSize: 100,
            draggable: true,
            symbol: "roundRect",
            roam: true,
            label: {
              show: true
            },
            edgeSymbol: ['none', 'arrow'],
            edgeSymbolSize: [1, 15],
            data: statusData,
            links: statusLink,
            lineStyle: {
              opacity: 0.9,
              width: 3,
              curveness: 0.2
            }
          }
        ]
      };
    };
  }
}
