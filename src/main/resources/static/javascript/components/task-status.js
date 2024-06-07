const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
:host {
    display: block;
    border: 1px solid #ccc;
    padding: 10px;
    margin: 10px;
    background-color: white;
}
`;

export class TaskStatus extends HTMLElement {
    #elements;

    constructor() {
        super();
        const shadowRoot = this.attachShadow({ mode: "open" });
        shadowRoot.adoptedStyleSheets = [styleSheet];

        const header = shadowRoot.appendChild(document.createElement("div"));

        const container = shadowRoot.appendChild(document.createElement("div"));
        container.appendChild(document.createElement("slot"));

        const createButton = shadowRoot.appendChild(document.createElement("button"));

        this.handleDragOver = this.handleDragOver.bind(this);
        this.handleDrop = this.handleDrop.bind(this);
        this.handleCreate = this.handleCreate.bind(this);

        this.#elements = { header, container, createButton };
    }

    get taskStatus() { return this.getAttribute("task-status"); }

    connectedCallback() {
        this.#elements.createButton.addEventListener("click", this.handleCreate);
        this.addEventListener("dragover", this.handleDragOver);
        this.addEventListener("drop", this.handleDrop);
    }

    disconnectedCallback() {
        this.#elements.createButton.removeEventListener("click", this.handleCreate);
        this.removeEventListener("dragover", this.handleDragOver);
        this.removeEventListener("drop", this.handleDrop);
    }

    handleDragOver(event) {
        event.preventDefault();
    }

    handleDrop(event) {
        event.preventDefault();

        const id = event.dataTransfer.getData("text/plain");
        this.dispatchEvent(new CustomEvent("task-dropped", {
            detail: {
                taskId: id,
                status: this.taskStatus
            },
            bubbles: true,
            composed: true
        }));
    }

    handleCreate() {

    }
}

customElements.define("task-status", TaskStatus);
