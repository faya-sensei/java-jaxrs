import { TASK_CREATED, TASK_EDITING } from "./task-event.js";

const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
:host {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) scale(0);
  opacity: 0;
  visibility: hidden;
  transition: transform 0.3s ease, opacity 0.3s ease, visibility 0.3s ease;
}

:host(.visible) {
  opacity: 1;
  visibility: visible;
  transform: translate(-50%, -50%) scale(1);
}

form {
  display: flex;
  flex-direction: column;
  min-width: 260px;
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-shadow: 3px 3px 5px rgba(0, 0, 0, 0.1);
  padding: 1.5rem 2rem;
}

input {
  margin: 0.5rem 0.25rem;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;

  &:focus {
    outline: none;
    border-color: #007bff;
  }
}

button {
  margin: 0.5rem 0;
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  
  cursor: pointer;

  &:first-of-type {
    margin-top: 0.75rem;
    background: grey;
    color: white;
  }

  &:last-of-type {
    background: blue;
    color: white;
  }

  &:hover {
    opacity: 0.9;
  }
}
`;

export class TaskForm extends HTMLElement {
    #elements = {};
    #data = {};

    constructor() {
        super();

        const shadowRoot = this.attachShadow({ mode: "open" });
        shadowRoot.adoptedStyleSheets = [styleSheet];

        const form = shadowRoot.appendChild(document.createElement("form"));

        const titleLabel = form.appendChild(document.createElement("label"));
        Object.assign(titleLabel, { for: "title", innerText: "Title" });
        const title = form.appendChild(document.createElement("input"));
        Object.assign(title, { id: "title", name: "title", type: "text" });
        const descriptionLabel = form.appendChild(document.createElement("label"));
        Object.assign(descriptionLabel, { for: "description", innerText: "Description" });
        const description = form.appendChild(document.createElement("input"));
        Object.assign(description, { id: "description", name: "description", type: "text" });
        const startDateLabel = form.appendChild(document.createElement("label"));
        Object.assign(startDateLabel, { for: "startDate", innerText: "Start Date" });
        const startDate = form.appendChild(document.createElement("input"));
        Object.assign(startDate, { id: "startDate", name: "startDate", type: "datetime-local" });
        const endDateLabel = form.appendChild(document.createElement("label"));
        Object.assign(endDateLabel, { for: "endDate", innerText: "End Date" });
        const endDate = form.appendChild(document.createElement("input"));
        Object.assign(endDate, { id: "endDate", name: "endDate", type: "datetime-local" });

        const cancel = form.appendChild(document.createElement("button"));
        cancel.innerText = "Cancel";
        const submit = form.appendChild(document.createElement("button"));
        submit.innerText = "Save";

        this.handleTaskEditing = this.handleTaskEditing.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);

        this.#elements = { form, title, description, startDate, endDate, cancel, submit };
    }

    get taskId() { return this.#data.id; }
    set taskId(value) { this.#data.id = value; }

    get taskTitle() { return this.#data.title; }
    set taskTitle(value) { this.#data.title = value; }

    get taskDescription() { return this.#data.description; }
    set taskDescription(value) { this.#data.description = value; }

    get taskStartDate() { return this.#data.startDate; }
    set taskStartDate(value) { this.#data.startDate = value; }

    get taskEndDate() { return this.#data.endDate; }
    set taskEndDate(value) { this.#data.endDate = value; }

    get taskStatus() { return this.#data.status; }
    set taskStatus(value) { this.#data.status = value; }

    connectedCallback() {
        this.#elements.form.addEventListener("submit", this.handleSubmit);
        this.#elements.cancel.addEventListener("click", this.handleCancel);
        this.addEventListener(TASK_EDITING, this.handleTaskEditing);
    }

    disconnectedCallback() {
        this.#elements.form.removeEventListener("submit", this.handleSubmit);
        this.#elements.cancel.removeEventListener("click", this.handleCancel);
        this.addEventListener(TASK_EDITING, this.handleTaskEditing);
    }

    handleSubmit(event) {
        event.preventDefault();

        const data = new FormData(event.target);

        this.dispatchEvent(new CustomEvent(TASK_CREATED, {
            bubbles: true,
            composed: true,
            detail: {
                status: this.taskStatus,
                task: {
                    ...Object.fromEntries(data.entries()),
                    id: this.taskId
                }
            }
        }));

        this.classList.toggle("visible", false);
    }

    handleCancel(event) {
        event.preventDefault();

        this.classList.toggle("visible", false);
    }

    handleTaskEditing(event) {
        event.preventDefault();

        const { id, title, description, startDate, endDate, status } = { ...event.detail, ...this.#data };

        this.#data = { id, title, description, startDate, endDate, status };

        this.updateElements();

        this.classList.toggle("visible", true);
    }

    updateElements() {
        const { title, description, startDate, endDate } = this.#elements;

        title.value = this.taskTitle;
        description.value = this.taskDescription;
        startDate.value = this.taskStartDate;
        endDate.value = this.taskEndDate;
    }
}

customElements.define("task-form", TaskForm);
