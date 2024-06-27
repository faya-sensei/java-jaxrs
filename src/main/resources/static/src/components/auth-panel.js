import { AUTH_AUTHORIZED, AUTH_LOADING } from "./auth-event.js";
import { login, register } from "../api.js";

const styleSheet = new CSSStyleSheet();
styleSheet.replaceSync`
:host {
  display: none;
  max-width: 360px;
  margin: 0 auto;
}

:host(.visible) {
  display: block;
}

form {
  display: flex;
  flex-direction: column;
  min-width: 260px;
}

input {
  margin: 0.5rem 0.25rem;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 6px;

  &:focus {
    outline: none;
    border-color: #007bff;
  }
}

.button-group {
  display: flex;
  gap: 1rem;
  justify-content: space-between;

  & > button {
    flex: 1;
    margin: 0.5rem 0;
    padding: 0.5rem;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    color: white;
    background-color: #1eb2d7;
    transition: background 0.3s;

    &:hover {
      background-color: #1d82b9;
    }
  }
}
`;

export class AuthPanel extends HTMLElement {
    #elements = {};

    constructor() {
        super();

        const shadowRoot = this.attachShadow({ mode: "open" });
        shadowRoot.adoptedStyleSheets = [styleSheet];

        const form = shadowRoot.appendChild(document.createElement("form"));

        const nameLabel = form.appendChild(document.createElement("label"));
        Object.assign(nameLabel, { for: "name", innerText: "Name" });
        const name = form.appendChild(document.createElement("input"));
        Object.assign(name, { id: "name", name: "name", type: "text", placeholder: "Please enter your account", required: true });
        const passwordLabel = form.appendChild(document.createElement("label"));
        Object.assign(passwordLabel, { for: "password", innerText: "Password" });
        const password = form.appendChild(document.createElement("input"));
        Object.assign(password, { id: "password", name: "password", type: "password", placeholder: "Please enter your password", required: true });

        const buttonGroup = form.appendChild(document.createElement("div"));
        buttonGroup.className = "button-group";

        const login = buttonGroup.appendChild(document.createElement("button"));
        login.dataset.name = "login";
        login.innerText = "Login";
        const register = buttonGroup.appendChild(document.createElement("button"));
        register.dataset.name = "register";
        register.innerText = "Register";

        this.handleAuthLoading = this.handleAuthLoading.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);

        this.#elements = { form, name, password };
    }

    connectedCallback() {
        this.#elements.form.addEventListener("submit", this.handleSubmit);
        this.addEventListener(AUTH_LOADING, this.handleAuthLoading);
    }

    disconnectedCallback() {
        this.#elements.form.removeEventListener("submit", this.handleSubmit);
        this.removeEventListener(AUTH_LOADING, this.handleAuthLoading);
    }

    handleAuthLoading() {
        Object.values(this.#elements).forEach(element => {
            if (element instanceof HTMLInputElement) {
                element.value = null;
            }
        });

        this.classList.toggle("visible", true);
    }

    handleSubmit(event) {
        event.preventDefault();

        const delegates = { "login": login, "register": register };
        const data = new FormData(event.target);

        delegates[event.submitter.dataset.name]?.(Object.fromEntries(data.entries()))
            .then(result => {
                if (result) {
                    this.dispatchEvent(new CustomEvent(AUTH_AUTHORIZED, {
                        detail: {
                            id: result.id
                        }
                    }));

                    this.classList.toggle("visible", false);
                }
            });
    }
}

customElements.define("auth-panel", AuthPanel);