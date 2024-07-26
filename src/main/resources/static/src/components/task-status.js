import { TASK_CREATED, TASK_UPDATED } from "./task-event.js";

const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
:host {
  display: flex;
  flex-direction: column;
  height: 100%;
  margin: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 6px;
}

.header {
  font-size: 1.2em;
  font-weight: bold;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #ccc;
}

.container {
  overflow: auto;
  flex: 1;
}

form {
  display: flex;
  padding: 0.75rem;
  gap: 0.5rem;

  &:hover {
    background-color: #d0d7de33;
  }

  & > input {
    padding: 0.5rem;
    border: 1px solid #ccc;
    border-radius: 6px;
    outline: none;
  }

  & > button {
    padding: 0.5rem 1rem;
    cursor: pointer;
    transition: background 0.3s;
    color: white;
    border: none;
    border-radius: 6px;
    background-color: #1eb2d7;

    &:hover {
      background-color: #1d82b9;
    }
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

        const form = shadowRoot.appendChild(document.createElement("form"));
        const title = form.appendChild(document.createElement("input"));
        Object.assign(title, { id: "title", name: "title", type: "text", placeholder: "Start typing a draft" })
        const create = form.appendChild(document.createElement("button"));
        create.innerText = "+ Add";

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleDragOver = this.handleDragOver.bind(this);
        this.handleDrop = this.handleDrop.bind(this);

        this.#elements = { header, container, form };
    }

    get taskStatus() { return this.#data.status; }
    set taskStatus(value) { this.#data.status = value; }

    connectedCallback() {
        this.#elements.form.addEventListener("submit", this.handleSubmit);
        this.addEventListener("dragover", this.handleDragOver);
        this.addEventListener("drop", this.handleDrop);
        this.updateElements();
    }

    disconnectedCallback() {
        this.#elements.form.removeEventListener("submit", this.handleSubmit);
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

    handleSubmit(event) {
        event.preventDefault();

        const formData = new FormData(event.target);

        this.dispatchEvent(new CustomEvent(TASK_CREATED, {
            bubbles: true,
            composed: true,
            detail: {
                status: this.taskStatus,
                task: Object.fromEntries(formData.entries())
            }
        }));
    }

    updateElements() {
        const { header } = this.#elements;

        header.innerText = this.taskStatus;
    }
}

customElements.define("task-status", TaskStatus);
