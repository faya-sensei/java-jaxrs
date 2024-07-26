import { PROJECT_FORWARD, PROJECT_LOADING, PROJECT_OPENING } from "./project-event.js";
import { ProjectComponent } from "./project-component.js";
import { getAllProjects, getProject, saveProject } from "../api.js";

const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
:host {
  display: none;
}

:host(.visible) {
  display: block;
}

form {
  display: flex;
  justify-content: space-between;

  & > input {
    flex: 1;
    margin: 0.5rem 0.25rem;
    padding: 0.5rem;
    border: 1px solid #ccc;
    border-radius: 6px;

    &:focus {
      border-color: #007bff;
      outline: none;
    }
  }

  & > button {
    margin: 0.5rem 0.25rem;
    padding: 0.5rem;
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

export class ProjectPanel extends HTMLElement {
    #projects = [];
    #elements = {};
    #data = {};

    constructor() {
        super();

        const shadowRoot = this.attachShadow({ mode: "open" });
        shadowRoot.adoptedStyleSheets = [styleSheet];

        const form = shadowRoot.appendChild(document.createElement("form"));

        const name = form.appendChild(document.createElement("input"));
        Object.assign(name, { name: "name", type: "text", placeholder: "Enter project name" });

        const submit = form.appendChild(document.createElement("button"));
        submit.innerText = "+ Create";

        const container = shadowRoot.appendChild(document.createElement("div"));
        container.className = "container";

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleProjectLoading = this.handleProjectLoading.bind(this);
        this.handleProjectOpening = this.handleProjectOpening.bind(this);

        this.#elements = { form, name, submit, container };
    }

    get ownerId() { return this.#data.ownerId; }
    set ownerId(value) { this.#data.ownerId = value; }

    connectedCallback() {
        this.#elements.form.addEventListener("submit", this.handleSubmit);
        this.addEventListener(PROJECT_LOADING, this.handleProjectLoading);
        this.addEventListener(PROJECT_OPENING, this.handleProjectOpening);
    }

    disconnectedCallback() {
        this.#elements.form.removeEventListener("submit", this.handleSubmit);
        this.removeEventListener(PROJECT_LOADING, this.handleProjectLoading);
        this.removeEventListener(PROJECT_OPENING, this.handleProjectOpening);
    }

    handleSubmit(event) {
        event.preventDefault();

        const data = new FormData(event.target);

        saveProject({ ...Object.fromEntries(data.entries()), ownerIds: [this.ownerId] }).then(project => {
            this.#projects.push(project);

            this.updateElements();
        });
    }

    handleProjectLoading(event) {
        this.ownerId = event.detail.id;

        getAllProjects().then(projects => {
            this.#projects.push(...projects);

            this.updateElements();

            this.classList.toggle("visible", true);
        });
    }

    handleProjectOpening(event) {
        const projectId = event.detail.id;

        getProject(projectId).then(project => {
            this.classList.toggle("visible", false);

            this.dispatchEvent(new CustomEvent(PROJECT_FORWARD, {
                detail: project
            }));
        });
    }

    updateElements() {
        const { container, name } = this.#elements;

        container.innerHTML = null;
        name.value = null;

        container.append(
            ...this.#projects.map(project => {
                const projectElement = new ProjectComponent();
                projectElement.projectId = project.id;
                projectElement.projectName = project.name;

                return projectElement;
            })
        );
    }
}

customElements.define("project-panel", ProjectPanel);
