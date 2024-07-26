import { TASK_UPDATED } from "./task-event.js";

const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
:host {
  display: block;
  margin: 0.5rem;
  padding: 0.5rem;
  cursor: move;
  border: 1px solid #ccc;
  border-radius: 6px;
  background-color: white;
}

form {
  display: flex;
  flex-direction: column;

  & > input {
    padding: 0.5rem;
    border: 1px solid #ccc;
    border-radius: 6px;
    outline: none;

    &[readonly] {
      cursor: pointer;
      border: 1px solid transparent;
    }
  }

  & > .button-bar {
    display: flex;
    justify-content: flex-end;

    & > button {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 6px;
      cursor: pointer;
      border: none;
      background: none;

      & > svg {
        width: 1rem;
        height: 1rem;
        fill: #666;
      }
    }
  }
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

        const buttonBar = form.appendChild(document.createElement("div"));
        buttonBar.className = "button-bar";

        const editButton = buttonBar.appendChild(document.createElement("button"));
        editButton.type = "button";
        editButton.innerHTML = `
          <svg viewBox="0 0 24 24">
            <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04a1.004 1.004 0 0 0 0-1.41L18.37 3.29a1.004 1.004 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
          </svg>
        `;

        const deleteButton = buttonBar.appendChild(document.createElement("button"));
        deleteButton.type = "button";
        deleteButton.innerHTML = `
          <svg viewBox="0 0 24 24">
            <path d="M16 9v10H8V9h8m-1.5-6h-5l-1 1H5v2h14V4h-4.5l-1-1z"/>
          </svg>
        `;

        const saveButton = buttonBar.appendChild(document.createElement("button"));
        saveButton.type = "submit";
        saveButton.innerHTML = `
          <svg viewBox="0 0 24 24">
            <path d="M5 13l4 4L19 7" stroke="currentColor" stroke-width="2" fill="none" />
          </svg>
        `;

        const cancelButton = buttonBar.appendChild(document.createElement("button"));
        cancelButton.type = "button";
        cancelButton.innerHTML = `
          <svg viewBox="0 0 24 24">
            <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" fill="none" />
          </svg>
        `;

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

        this.handleEditClick = this.handleEditClick.bind(this);
        this.handleRemoveClick = this.handleRemoveClick.bind(this);
        this.handleSaveClick = this.handleSaveClick.bind(this);
        this.handleCancelClick = this.handleCancelClick.bind(this);
        this.handleDragStart = this.handleDragStart.bind(this);

        this.#elements = { form, title, description, startDate, endDate, editButton, deleteButton, saveButton, cancelButton };
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
        this.#elements.editButton.addEventListener("click", this.handleEditClick);
        this.#elements.deleteButton.addEventListener("click", this.handleRemoveClick);
        this.#elements.form.addEventListener("submit", this.handleSaveClick);
        this.#elements.cancelButton.addEventListener("click", this.handleCancelClick);
        this.addEventListener("dragstart", this.handleDragStart);
        this.updateElements();
    }

    disconnectedCallback() {
        this.#elements.editButton.removeEventListener("click", this.handleEditClick);
        this.#elements.deleteButton.removeEventListener("click", this.handleRemoveClick);
        this.#elements.form.removeEventListener("submit", this.handleSaveClick);
        this.#elements.cancelButton.addEventListener("click", this.handleCancelClick);
        this.removeEventListener("dragstart", this.handleDragStart);
    }

    handleEditClick(event) {
        event.preventDefault();
        event.stopPropagation();

        this.editMode = true;
        this.updateElements();
    }

    handleRemoveClick(event) {
        event.preventDefault();
        event.stopPropagation();

        // Call delete
    }

    handleSaveClick(event) {
        event.preventDefault();
        event.stopPropagation();

        const formData = new FormData(event.target);

        const payload = {
            ...this.#data,
            ...Object.fromEntries(
                Object.entries(Object.fromEntries(formData.entries()))
                    .filter(([k, v]) => !!k && !!v)
            )
        };

        if (JSON.stringify(this.#data) !== JSON.stringify(payload)) {
            this.#data = payload;
            this.dispatchEvent(new CustomEvent(TASK_UPDATED, {
                bubbles: true,
                composed: true,
                detail: {
                    task: payload
                }
            }));
        }
    }

    handleCancelClick(event) {
        event.preventDefault();
        event.stopPropagation();

        this.editMode = false;
        this.updateElements()
    }

    handleDragStart(event) {
        event.dataTransfer.setData("text/plain", this.taskId);
    }

    updateElements() {
        const { title, description, startDate, endDate, editButton, deleteButton, saveButton, cancelButton } = this.#elements;

        title.value = this.taskTitle;
        description.value = this.taskDescription ?? null;
        // Format the date and time to 'YYYY-MM-DDTHH:MM'
        startDate.value = this.taskStartDate ? new Date(this.taskStartDate).toISOString().slice(0, 16) : null;
        endDate.value = this.taskEndDate ? new Date(this.taskEndDate).toISOString().slice(0, 16) : null;

        if (this.editMode) {
            this.draggable = false;
            [title, description, startDate, endDate].forEach(input => {
                input.toggleAttribute("readonly", false);
            });
            editButton.style.display = "none";
            deleteButton.style.display = "none";
            saveButton.style.display = "flex";
            cancelButton.style.display = "flex";
        } else {
            this.draggable = true;
            [title, description, startDate, endDate].forEach(input => {
                input.toggleAttribute("readonly", true);
            });
            editButton.style.display = "flex";
            deleteButton.style.display = "flex";
            saveButton.style.display = "none";
            cancelButton.style.display = "none";
        }
    }
}

customElements.define("task-component", TaskComponent);
