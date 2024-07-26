import { TaskComponent } from "./task-component.js";
import { TASK_CREATED, TASK_LOADING, TASK_UPDATED } from "./task-event.js";
import { TaskStatus } from "./task-status.js";
import { listenTask, saveTask, updateTask } from "../api.js";

const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
:host {
  display: none;
}

:host(.visible) {
  display: block;
}

.container {
  display: flex;
}
`;

export class TaskPanel extends HTMLElement {
    /** @type {Map<String, {statusComponent: TaskStatus, statusData: Map<int, {taskData: TaskDTO, taskComponent: TaskComponent}>}>} */
    #taskStatuses = new Map();
    #elements = {};
    #data = {};

    constructor() {
        super();

        const shadowRoot = this.attachShadow({ mode: "open" });
        shadowRoot.adoptedStyleSheets = [styleSheet];

        const container = shadowRoot.appendChild(document.createElement("div"));
        container.className = "container";

        this.handleTaskLoading = this.handleTaskLoading.bind(this);
        this.handleTaskCreated = this.handleTaskCreated.bind(this);
        this.handleTaskUpdated = this.handleTaskUpdated.bind(this);

        this.#elements = { container };
    }

    get projectId() { return this.#data.projectId; }
    set projectId(value) { this.#data.projectId = value; }

    connectedCallback() {
        this.addEventListener(TASK_LOADING, this.handleTaskLoading);
        this.addEventListener(TASK_CREATED, this.handleTaskCreated);
        this.addEventListener(TASK_UPDATED, this.handleTaskUpdated);
    }

    disconnectedCallback() {
        this.removeEventListener(TASK_LOADING, this.handleTaskLoading)
        this.removeEventListener(TASK_CREATED, this.handleTaskCreated);
        this.removeEventListener(TASK_UPDATED, this.handleTaskUpdated);
    }

    handleTaskLoading(event) {
        const { id, tasks } = event.detail;

        this.projectId = id;

        for (const task of tasks) this.createTaskElement(task);

        listenTask(event => console.log(event));

        this.classList.toggle("visible", true);
    }

    handleTaskCreated(event) {
        const { status, task } = event.detail;

        console.log(`[Task Component] Task: ${task.title} created under status: ${status}.`);

        saveTask({ ...task, status, projectId: this.projectId }).then(task => this.createTaskElement(task));
    }

    handleTaskUpdated(event) {
        const { position, task: { id, status, ...payload } } = event.detail;

        console.log(`[Task Component] Task id: ${id} at status: ${status}.`, position);

        updateTask({ id, status, ...payload }).then(task => this.updateTaskElement(task));
    }

    createTaskElement(task) {
        const taskComponent = new TaskComponent();
        taskComponent.taskId = task.id;
        taskComponent.taskTitle = task.title;
        taskComponent.taskDescription = task.description;
        taskComponent.taskStartDate = task.startDate;
        taskComponent.taskEndDate = task.endDate;

        const { statusComponent, statusData } = !this.#taskStatuses.has(task.status)
            ? this.createStatusComponent(task.status)
            : this.#taskStatuses.get(task.status);

        if (!statusData.has(task.id)) {
            statusComponent.append(taskComponent);
            statusData.set(task.id, { taskComponent, taskData: task });
        }
    }

    createStatusComponent(status) {
        const statusComponent = new TaskStatus();
        statusComponent.taskStatus = status;

        const statusData = new Map();

        this.#elements.container.append(statusComponent);
        this.#taskStatuses.set(status, { statusComponent, statusData });

        return { statusComponent, statusData };
    }

    updateTaskElement(task) {
        const currentStatus = [...this.#taskStatuses].find(([_, { statusData }]) => statusData.has(task.id))?.[1];
        const currentTask = currentStatus?.statusData.get(task.id);
        const nextStatus = this.#taskStatuses.get(task.status);

        if (currentTask && currentStatus) {
            currentStatus.statusData.set(task.id, { ...currentTask.taskData, ...task });
            currentTask.taskComponent.taskTitle = task.title ?? currentTask.taskComponent.taskTitle;
            currentTask.taskComponent.taskDescription = task.description ?? currentTask.taskComponent.taskDescription;
            currentTask.taskComponent.taskStartDate = task.startDate ?? currentTask.taskComponent.taskStartDate;
            currentTask.taskComponent.taskEndDate = task.endDate ?? currentTask.taskComponent.taskEndDate;
            currentTask.taskComponent.updateElements();

            if (currentStatus !== nextStatus && nextStatus) {
                currentTask.taskComponent.remove();
                currentStatus.statusData.delete(task.id);

                nextStatus.statusComponent.append(currentTask.taskComponent);
                nextStatus.statusData.set(task.id, currentTask);
            }
        }
    }
}

customElements.define("task-panel", TaskPanel);
