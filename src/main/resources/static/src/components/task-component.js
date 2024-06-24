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
`;

export class TaskComponent extends HTMLElement {
    #elements = {};
    #data = {};

    constructor() {
        super();
        this.draggable = true;

        const shadowRoot = this.attachShadow({ mode: "open" });
        shadowRoot.adoptedStyleSheets = [styleSheet];

        const container = shadowRoot.appendChild(document.createElement("div"));

        const title = container.appendChild(document.createElement("div"));
        const description = container.appendChild(document.createElement("div"));
        const startDate = container.appendChild(document.createElement("div"));
        const endDate = container.appendChild(document.createElement("div"));

        this.handleDragStart = this.handleDragStart.bind(this);

        this.#elements = { title, description, startDate, endDate };
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
        this.addEventListener("dragstart", this.handleDragStart);
        this.updateElements();
    }

    disconnectedCallback() {
        this.removeEventListener("dragstart", this.handleDragStart);
    }

    handleDragStart(event) {
        event.dataTransfer.setData("text/plain", this.taskId);
    }

    updateElements() {
        const { title, description, startDate, endDate } = this.#elements;

        title.innerText = this.taskTitle;
        description.innerText = this.taskDescription;
        startDate.innerText = this.taskStartDate;
        endDate.innerText = this.taskEndDate;
    }
}

customElements.define("task-component", TaskComponent);
