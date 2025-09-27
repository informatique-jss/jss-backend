import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { toTitleCase } from '../../../../libs/inspinia/utils/string-utils';
import { UiCardComponent } from '../ui-card/ui-card.component';

type TaskType = {
  title: string;
  dueDays: number;
  status: "in-progress" | "completed" | "on-hold" | "out-dated"
  assignBy: {
    name: string;
    avatar: string;
    email: string;
  },
  startDate: string;
  priority: "high" | "low" | "medium",
  progress: number,
  totalTime: string;
}

@Component({
  selector: 'tasks-widget',
  imports: [UiCardComponent, RouterLink],
  standalone: true,
  templateUrl: './tasks-widget.component.html',
  styles: ``
})
export class TasksWidgetComponent implements OnInit {

  toTitleCase = toTitleCase
  isLoading = false;
  tasks: TaskType[] = [
    {
      title: 'Admin Dashboard Template - Final Touch',
      dueDays: 2,
      status: 'in-progress',
      assignBy: {
        name: 'Liam Johnson',
        avatar: 'assets/images/users/user-3.jpg',
        email: 'liam@pixelcraft.io',
      },
      startDate: 'Apr 15, 2025',
      priority: 'high',
      progress: 70,
      totalTime: '8h 45min',
    },
    {
      title: 'Tailwind UI Kit Design',
      dueDays: 10,
      status: 'completed',
      assignBy: {
        name: 'Ava Reynolds',
        avatar: 'assets/images/users/user-5.jpg',
        email: 'ava@designwave.co',
      },
      startDate: 'Mar 29, 2025',
      priority: 'low',
      progress: 100,
      totalTime: '34h 10min',
    },
    {
      title: 'React + Next.js Starter Template',
      dueDays: 5,
      status: 'in-progress',
      assignBy: {
        name: 'Noah Carter',
        avatar: 'assets/images/users/user-2.jpg',
        email: 'noah@devspark.com',
      },
      startDate: 'Apr 12, 2025',
      priority: 'medium',
      progress: 45,
      totalTime: '14h 25min',
    },
    {
      title: 'Laravel Template Docs Update',
      dueDays: 4,
      status: 'on-hold',
      assignBy: {
        name: 'Sophia Bennett',
        avatar: 'assets/images/users/user-1.jpg',
        email: 'sophia@codepress.io',
      },
      startDate: 'Apr 10, 2025',
      priority: 'low',
      progress: 30,
      totalTime: '6h 50min',
    },
    {
      title: 'Portfolio Website Redesign',
      dueDays: 12,
      status: 'out-dated',
      assignBy: {
        name: 'Mason Clark',
        avatar: 'assets/images/users/user-6.jpg',
        email: 'mason@webgenius.dev',
      },
      startDate: 'Apr 01, 2025',
      priority: 'high',
      progress: 10,
      totalTime: '11h 30min',
    },
  ];

  ngOnInit(): void {
    this.loadTasks();
  }

  onScroll(event: Event) {
    const target = event.target as HTMLElement;
    const threshold = 50; // px before end to trigger

    if (target.scrollTop + target.clientHeight >= target.scrollHeight - threshold) {
      this.loadMoreTasks();
    }
  }

  loadTasks() {
    // TODO: appel backend initial
  }

  loadMoreTasks() {
    if (this.isLoading) return;
    this.isLoading = true;

    // TODO: appel backend pour charger plus de tâches
    // Simu:
    setTimeout(() => {
      // this.tasks.push(...nouvellesTaches)
      this.isLoading = false;
    }, 1000);
  }

}
