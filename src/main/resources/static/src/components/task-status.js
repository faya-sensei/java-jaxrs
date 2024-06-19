import { TaskBoard } from "./task-board.js";
import { TASK_EDITING, TASK_UPDATED } from "./task-event.js";

const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
:host {
  height: 100%;
  display: flex;
  flex-direction: column;
  border: 1px solid #ccc;
  border-radius: 6px;
  margin: 0.5rem;
}

.header {
  font-size: 1.2em;
  font-weight: bold;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #ccc;
}

.container {
  flex: 1;
  overflow: auto;
}

.footer {
  font-weight: bold;
  padding: 0.75rem;
  border: none;
  cursor: pointer;

  &:hover {
    background-color: #d0d7de33;
  }
}
`;

export class TaskStatus extends HTMLElement {
    #eventManager = TaskBoard.eventManager;
    #elements = {};
    #data = {};

    constructor() {
        super();
        const shadowRoot = this.attachShadow({ mode: "open" });
        shadowRoot.adoptedStyleSheets = [styleSheet];

        const header = shadowRoot.appendChild(document.createElement("div"));
        header.className = "header";

        const container = shadowRoot.appendChild(document.createElement("div"));
        container.className = "container";
        container.appendChild(document.createElement("slot"));

        const footer = shadowRoot.appendChild(document.createElement("div"));
        footer.className = "footer";
        const createButton = footer.appendChild(document.createElement("span"));
        createButton.innerText = "+ Add item";

        this.handleDragOver = this.handleDragOver.bind(this);
        this.handleDrop = this.handleDrop.bind(this);
        this.handleCreate = this.handleCreate.bind(this);

        this.#elements = { header, container, footer };
    }

    get taskStatus() { return this.#data.taskStatus; }
    set taskStatus(value) { this.#data.taskStatus = value; }

    connectedCallback() {
        this.#elements.footer.addEventListener("click", this.handleCreate);
        this.addEventListener("dragover", this.handleDragOver);
        this.addEventListener("drop", this.handleDrop);
        this.updateElements();
    }

    disconnectedCallback() {
        this.#elements.footer.removeEventListener("click", this.handleCreate);
        this.removeEventListener("dragover", this.handleDragOver);
        this.removeEventListener("drop", this.handleDrop);
    }

    handleDragOver(event) {
        event.preventDefault();
    }

    handleDrop(event) {
        event.preventDefault();

        const taskId = event.dataTransfer.getData("text");
        const rect = this.#elements.container.getBoundingClientRect();
        this.#eventManager.dispatchEvent(new CustomEvent(TASK_UPDATED, {
            detail: {
                position: {
                    x: event.clientX,
                    y: event.clientY,
                    localX: event.clientX - rect.left,
                    localY: event.clientX - rect.top
                },
                task: {
                    id: taskId,
                    status: this.taskStatus,
                }
            }
        }));
    }

    handleCreate() {
        this.#eventManager.dispatchEvent(new CustomEvent(TASK_EDITING, {
            detail: {
                status: this.taskStatus
            }
        }));
    }

    updateElements() {
        const { header } = this.#elements;

        header.innerText = this.taskStatus;
    }
}

customElements.define("task-status", TaskStatus);
