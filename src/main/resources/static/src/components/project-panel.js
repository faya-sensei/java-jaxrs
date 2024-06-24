import { PROJECT_FORWARD, PROJECT_LOADING } from "./project-event.js";
import { ProjectComponent } from "./project-component.js";
import { getAllProjects } from "../api.js";

const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
:host {
  display: none;
}

:host(.visible) {
  display: block;
}
`;

export class ProjectPanel extends HTMLElement {
    #projects = [];
    #elements = {};

    constructor() {
        super();

        const shadowRoot = this.attachShadow({ mode: "open" });
        shadowRoot.adoptedStyleSheets = [styleSheet];

        const container = shadowRoot.appendChild(document.createElement("div"));
        container.className = "container";

        this.handleProjectLoading = this.handleProjectLoading.bind(this);
        this.handleProjectForward = this.handleProjectForward.bind(this);

        this.#elements = { container };
    }

    connectedCallback() {
        this.addEventListener(PROJECT_LOADING, this.handleProjectLoading);
        this.addEventListener(PROJECT_FORWARD, this.handleProjectForward);
    }

    disconnectedCallback() {
        this.removeEventListener(PROJECT_LOADING, this.handleProjectLoading);
        this.removeEventListener(PROJECT_FORWARD, this.handleProjectForward);
    }

    handleProjectLoading() {
        getAllProjects().then(projects => {
            this.#projects.push(...projects);

            this.updateElements();
        });

        this.classList.toggle("visible", true);
    }

    handleProjectForward() {
        this.classList.toggle("visible", false);
    }

    updateElements() {
        const { container } = this.#elements;

        container.innerHTML = null;

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
