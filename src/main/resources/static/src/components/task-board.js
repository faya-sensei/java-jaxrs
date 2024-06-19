import { getAllTasks, saveTask } from "../api.js";
import { TaskComponent } from "./task-component.js";
import { TASK_CREATED, TASK_UPDATED } from "./task-event.js";
import { TaskStatus } from "./task-status.js";
import { TaskForm } from "./task-form.js";

const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
.container {
  display: flex;
}
`;

export class TaskBoard extends HTMLElement {
    static eventManager = new EventTarget();
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

        const modal = shadowRoot.appendChild(new TaskForm());

        this.handleTaskCreated = this.handleTaskCreated.bind(this);
        this.handleTaskUpdated = this.handleTaskUpdated.bind(this);

        this.#elements = { container, modal };
    }

    get boardId() { return this.#data.boardId; }
    set boardId(value) { this.#data.boardId = value; }

    connectedCallback() {
        TaskBoard.eventManager.addEventListener(TASK_CREATED, this.handleTaskCreated);
        TaskBoard.eventManager.addEventListener(TASK_UPDATED, this.handleTaskUpdated);

        getAllTasks().then(tasks => {
            for (const task of tasks) {
                if (this.#taskStatuses.has(task.status)) {
                    this.#taskStatuses.set(task.status, [...this.#taskStatuses.get(task.status), task]);
                } else {
                    this.#taskStatuses.set(task.status, [task]);
                }
            }

            this.updateElements();
        });
    }

    disconnectedCallback() {
        TaskBoard.eventManager.removeEventListener(TASK_CREATED, this.handleTaskCreated);
        TaskBoard.eventManager.removeEventListener(TASK_UPDATED, this.handleTaskUpdated);
    }

    handleTaskCreated(event) {
        const { status, task } = event.detail;

        console.log(`[Task Component] Task: ${task.title} created under status: ${status}.`);

        saveTask({ ...task, status, boardId: this.boardId }).then(task => {
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
                saveTask({ id, ...payload }).then(task => {
                    this.#taskStatuses.get(previousStatus)[index] = task;
                });
            } else {
                this.#taskStatuses.set(status, tasks.filter(task => task.id !== id));

                saveTask({ id, status }).then(task => {
                    this.#taskStatuses.set(previousStatus, [...this.#taskStatuses.get(status), task]);
                });
            }
            break;
        }
    }

    updateElements() {
        const { container } = this.#elements;

        container.innerHTML = null;

        for (let [status, tasks] of this.#taskStatuses) {
            const statusElement = new TaskStatus();
            statusElement.taskStatus = status;

            statusElement.append(
                ...tasks.map(task => {
                    const taskElement = new TaskComponent();
                    taskElement.taskId = task.id;
                    taskElement.taskTitle = task.title;
                    taskElement.taskDescription = task.description;
                    taskElement.taskStartDate = task.startDate;
                    taskElement.taskEndDate = task.endDate;

                    return taskElement;
                })
            );

            container.append(statusElement);
        }
    }
}

customElements.define("task-board", TaskBoard);
