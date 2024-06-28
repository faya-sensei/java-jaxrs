import { TASK_UPDATED } from "./task-event.js";

const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
:host {
  display: block;
  border: 1px solid #ccc;
  border-radius: 6px;
  padding: 10px;
  margin: 10px;
  background-color: white;
  cursor: move;
}

form {
  display: flex;
  flex-direction: column;
}
`;

export class TaskComponent extends HTMLElement {
    #elements = {};
    #data = {};

    constructor() {
        super();
        this.draggable = true;

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
        Object.assign(description, { id: "description", name: "description", type: "text", placeholder: "No description provided" });
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

        this.handleDragStart = this.handleDragStart.bind(this);
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

    connectedCallback() {
        this.#elements.form.addEventListener("submit", this.handleSubmit);
        this.#elements.cancel.addEventListener("click", this.handleCancel);
        this.addEventListener("dragstart", this.handleDragStart);
        this.updateElements();
    }

    disconnectedCallback() {
        this.#elements.form.removeEventListener("submit", this.handleSubmit);
        this.#elements.cancel.removeEventListener("click", this.handleCancel);
        this.removeEventListener("dragstart", this.handleDragStart);
    }

    handleSubmit(event) {
        event.preventDefault();

        const formData = new FormData(event.target);

        this.#data = {
            ...this.#data,
            ...Object.fromEntries(
                Object.entries(Object.fromEntries(formData.entries()))
                    .filter(([k, v]) => !!k && !!v)
            )
        };

        this.dispatchEvent(new CustomEvent(TASK_UPDATED, {
            bubbles: true,
            composed: true,
            detail: {
                status: this.taskStatus,
                task: this.#data
            }
        }));
    }

    handleCancel(event) {
        event.preventDefault();
    }

    handleDragStart(event) {
        event.dataTransfer.setData("text/plain", this.taskId);
    }

    updateElements() {
        const { title, description, startDate, endDate } = this.#elements;

        title.value = this.taskTitle;
        description.value = this.taskDescription ?? null;
        // Format the date and time to 'YYYY-MM-DDTHH:MM'
        startDate.value = this.taskStartDate ? new Date(this.taskStartDate).toISOString().slice(0, 16) : null;
        endDate.value = this.taskEndDate ? new Date(this.taskEndDate).toISOString().slice(0, 16) : null;
    }
}

customElements.define("task-component", TaskComponent);
