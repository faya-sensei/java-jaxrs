import { TaskComponent } from "./task-component.js";
import { TASK_CREATED, TASK_LOADING, TASK_UPDATED } from "./task-event.js";
import { TaskStatus } from "./task-status.js";
import { saveTask, updateTask } from "../api.js";

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
    /** @type {Map<String, TaskDTO[]>} */
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

        for (const task of tasks) {
            if (this.#taskStatuses.has(task.status)) {
                this.#taskStatuses.set(task.status, [...this.#taskStatuses.get(task.status), task]);
            } else {
                this.#taskStatuses.set(task.status, [task]);
            }
        }

        this.updateElements();

        this.classList.toggle("visible", true);
    }

    handleTaskCreated(event) {
        const { status, task } = event.detail;

        console.log(`[Task Component] Task: ${task.title} created under status: ${status}.`);

        saveTask({ ...task, status, projectId: this.projectId }).then(task => {
            if (this.#taskStatuses.has(status)) {
                this.#taskStatuses.set(status, [...this.#taskStatuses.get(status), task]);
            } else {
                this.#taskStatuses.set(status, [task]);
            }

            this.updateElements();
        });
    }

    handleTaskUpdated(event) {
        const { position, task: { id, status, ...payload } } = event.detail;

        console.log(`[Task Component] Task id: ${id} at status: ${status}.`, position);

        for (const [previousStatus, tasks] of this.#taskStatuses) {
            const index = tasks.find(task => task.id === id);
            if (index < 0) continue;

            this.#taskStatuses.set(status, tasks.filter(task => task.id !== id));

            if (previousStatus === status) {
                updateTask({ id, ...payload }).then(task => {
                    this.#taskStatuses.get(previousStatus)[index] = task;
                });
            } else {
                this.#taskStatuses.set(status, tasks.filter(task => task.id !== id));

                updateTask({ id, status }).then(task => {
                    this.#taskStatuses.set(previousStatus, [...this.#taskStatuses.get(status), task]);
                });
            }
        }
    }

    updateElements() {
        const { container } = this.#elements;

        container.innerHTML = null;

        for (const [status, tasks] of this.#taskStatuses) {
            const statusElement = new TaskStatus();
            statusElement.taskStatus = status;

            statusElement.append(
                ...tasks.map(task => {
                    const taskElement = new TaskComponent();
                    taskElement.taskId = task.id;
                    taskElement.taskTitle = task.title;
                    taskElement.taskDescription = task.description;
                    taskElement.taskStartDate = new Date(task.startDate);
                    taskElement.taskEndDate = new Date(task.endDate);

                    return taskElement;
                })
            );

            container.append(statusElement);
        }
    }
}

customElements.define("task-panel", TaskPanel);
