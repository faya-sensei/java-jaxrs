import { PROJECT_FORWARD, PROJECT_OPENING } from "./project-event.js";
import { getProject } from "../api.js";

const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
:host {
  display: block;
  margin: 0.5rem;
  padding: 0.5rem;
  cursor: pointer;
  border: 1px solid #ccc;
  border-radius: 6px;
  background-color: white;
}

form {
  display: flex;

  & > input {
    flex: 1;
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

export class ProjectComponent extends HTMLElement {
    #elements = {};
    #data = {};

    constructor() {
        super();

        const shadowRoot = this.attachShadow({ mode: "open" });
        shadowRoot.adoptedStyleSheets = [styleSheet];

        const form = shadowRoot.appendChild(document.createElement("form"));

        const name = form.appendChild(document.createElement("input"));
        Object.assign(name, { id: "name", name: "name", type: "text" });

        const buttonBar = form.appendChild(document.createElement("div"));
        buttonBar.className = "button-bar";

        const editButton = buttonBar.appendChild(document.createElement("button"));
        editButton.type = "button"
        editButton.innerHTML = `
          <svg viewBox="0 0 24 24">
            <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04a1.004 1.004 0 0 0 0-1.41L18.37 3.29a1.004 1.004 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
          </svg>
        `;

        const deleteButton = buttonBar.appendChild(document.createElement("button"));
        deleteButton.type = "button"
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

        this.handleEditClick = this.handleEditClick.bind(this);
        this.handleRemoveClick = this.handleRemoveClick.bind(this);
        this.handleSaveClick = this.handleSaveClick.bind(this);
        this.handleCancelClick = this.handleCancelClick.bind(this);
        this.handleClick = this.handleClick.bind(this);

        this.#elements = { form, name, editButton, deleteButton, saveButton, cancelButton };
    }

    get projectId() { return this.#data.id; }
    set projectId(value) { this.#data.id = value; }

    get projectName() { return this.#data.name; }
    set projectName(value) { this.#data.name = value; }

    connectedCallback() {
        this.#elements.editButton.addEventListener("click", this.handleEditClick);
        this.#elements.deleteButton.addEventListener("click", this.handleRemoveClick);
        this.#elements.form.addEventListener("submit", this.handleSaveClick);
        this.#elements.cancelButton.addEventListener("click", this.handleCancelClick);
        this.addEventListener("click", this.handleClick);
        this.updateElements();
    }

    disconnectedCallback() {
        this.#elements.editButton.removeEventListener("click", this.handleEditClick);
        this.#elements.deleteButton.removeEventListener("click", this.handleRemoveClick);
        this.#elements.form.removeEventListener("submit", this.handleSaveClick);
        this.#elements.cancelButton.removeEventListener("click", this.handleCancelClick);
        this.removeEventListener("click", this.handleClick);
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

        const updatedName = this.#elements.name.value.trim();

        if (updatedName && updatedName !== this.projectName) {
            // Call update
        } else {
            this.editMode = false;
            this.updateElements();
        }
    }

    handleCancelClick(event) {
        event.preventDefault();
        event.stopPropagation();

        this.editMode = false;
        this.updateElements()
    }

    handleClick(event) {
        if (this.editMode) return;

        event.preventDefault();
        event.stopPropagation();

        this.dispatchEvent(new CustomEvent(PROJECT_OPENING, {
            bubbles: true,
            composed: true,
            detail: {
                id: this.projectId
            }
        }));
    }

    updateElements() {
        const { name, editButton, deleteButton, saveButton, cancelButton } = this.#elements;

        name.value = this.projectName;

        if (this.editMode) {
            name.toggleAttribute("readonly", false);
            name.focus();
            editButton.style.display = "none";
            deleteButton.style.display = "none";
            saveButton.style.display = "flex";
            cancelButton.style.display = "flex";
        } else {
            name.toggleAttribute("readonly", true);
            name.blur();
            editButton.style.display = "flex";
            deleteButton.style.display = "flex";
            saveButton.style.display = "none";
            cancelButton.style.display = "none";
        }
    }
}

customElements.define("project-component", ProjectComponent);