import { TASK_CREATED, TASK_EDITING, TASK_UPDATED } from "./task-event.js";

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
        const title = footer.appendChild(document.createElement("input"));
        Object.assign(title, { id: "title", name: "title", type: "text", placeholder: "Start typing to create a draft" })
        const create = footer.appendChild(document.createElement("span"));
        create.innerText = "+ Add item";

        this.handleDragOver = this.handleDragOver.bind(this);
        this.handleDrop = this.handleDrop.bind(this);
        this.handleCreate = this.handleCreate.bind(this);

        this.#elements = { header, container, footer };
    }

    get taskStatus() { return this.#data.status; }
    set taskStatus(value) { this.#data.status = value; }

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

        const taskId = Number(event.dataTransfer.getData("text"));
        if (Number.isNaN(taskId)) return;

        const rect = this.#elements.container.getBoundingClientRect();
        this.dispatchEvent(new CustomEvent(TASK_UPDATED, {
            bubbles: true,
            composed: true,
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
        this.dispatchEvent(new CustomEvent(TASK_CREATED, {
            bubbles: true,
            composed: true,
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
