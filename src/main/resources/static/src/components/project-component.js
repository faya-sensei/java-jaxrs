import { PROJECT_FORWARD } from "./project-event.js";
import { getProject } from "../api.js";

const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
:host {
  display: block;
  border: 1px solid #ccc;
  border-radius: 6px;
  padding: 10px;
  margin: 10px;
  background-color: white;
  cursor: pointer;
}
`;

export class ProjectComponent extends HTMLElement {
    #elements = {};
    #data = {};

    constructor() {
        super();

        const shadowRoot = this.attachShadow({ mode: "open" });
        shadowRoot.adoptedStyleSheets = [styleSheet];

        const name = shadowRoot.appendChild(document.createElement("div"));

        this.handleClick = this.handleClick.bind(this);

        this.#elements = { name };
    }

    get projectId() { return this.#data.id; }
    set projectId(value) { this.#data.id = value; }

    get projectName() { return this.#data.name; }
    set projectName(value) { this.#data.name = value; }

    connectedCallback() {
        this.addEventListener("click", this.handleClick);
        this.updateElements();
    }

    disconnectedCallback() {
        this.removeEventListener("click", this.handleClick);
    }

    handleClick() {
        getProject(this.projectId).then(project => {
            this.dispatchEvent(new CustomEvent(PROJECT_FORWARD, {
                bubbles: true,
                composed: true,
                detail: project
            }))
        });
    }

    updateElements() {
        const { name } = this.#elements;

        name.innerText = this.projectName;
    }
}

customElements.define("project-component", ProjectComponent);