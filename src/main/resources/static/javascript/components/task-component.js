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

export class TaskComponent extends HTMLElement {
    #elements;

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

    get taskId() { return Number.parseInt(this.getAttribute("task-id")); }

    get taskTitle() { return this.getAttribute("task-title"); }

    get taskDescription() { return this.getAttribute("task-description"); }

    get taskStartDate() { return new Date(this.getAttribute("task-startDate")); }

    get taskEndDate() { return new Date(this.getAttribute("task-endDate")); }

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
