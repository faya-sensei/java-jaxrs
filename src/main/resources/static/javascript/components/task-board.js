const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
:host {
    display: block;
    border: 1px solid #ccc;
    padding: 10px;
    margin: 10px;
    background-color: white;
    cursor: move;
}
`;

export class TaskBoard extends HTMLElement {
    /** @type {Map<String, TaskDTO[]>} */
    #taskStatuses = new Map();
    #elements;

    constructor() {
        super();
        const shadowRoot = this.attachShadow({ mode: "open" });
        shadowRoot.adoptedStyleSheets = [styleSheet];

        const container = shadowRoot.appendChild(document.createElement("div"));
        container.className = "board";

        this.handleTaskDropped = this.handleTaskDropped.bind(this);
        this.handleTaskCreated = this.handleTaskCreated.bind(this);

        this.#elements = {
            container
        };
    }

    connectedCallback() {
        this.addEventListener("task-dropped", this.handleTaskDropped);
        this.addEventListener("task-created", this.handleTaskCreated);
    }

    disconnectedCallback() {
        this.removeEventListener("task-dropped", this.handleTaskDropped);
        this.removeEventListener("task-created", this.handleTaskCreated);
    }

    handleTaskDropped(event) {
        const { taskId, status } = event.detail;

        for (let [status, tasks] of this.#taskStatuses) {
            this.#taskStatuses.set(status, tasks.filter(task => task.id !== taskId));
        }

        const task = Array.from(this.#taskStatuses.values()).flat().find(task => task.id === taskId);
        task.status = status;

        this.#taskStatuses.get(status).push(task);
        this.render();
    }

    handleTaskCreated(event) {
    }

    updateElements() {
        const { container } = this.#elements;

        container.innerHTML = null;

        for (let [status, tasks] of this.#taskStatuses) {
            const statusElement = container.appendChild(document.createElement("task-status"));
            statusElement.setAttribute("task-status", status);

            tasks.map(task => {
                const taskElement = statusElement.appendChild(document.createElement("task-component"));
                taskElement.setAttribute("task-id", task.id);
                taskElement.setAttribute("task-title", task.title);
                taskElement.setAttribute("task-description", task.description);
                taskElement.setAttribute("task-startDate", task.startDate);
                taskElement.setAttribute("task-endDate", task.endDate);
            });
        }
    }
}

customElements.define("task-board", TaskBoard);