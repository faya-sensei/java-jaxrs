/**
 * @typedef ProjectDTO Project data transfer object.
 * @property {number} [id] The id of the project.
 * @property {string} name The name of the project.
 * @property {TaskDTO[]} tasks The tasks of the project.
 */

/**
 * @typedef UserDTO User data transfer object.
 * @property {number} [id] The id of the user.
 * @property {string} name The name of the user.
 * @property {string} [password] The password of the user.
 * @property {string} [role] The role of the user.
 * @property {string} [token] The token of the user.
 */

/**
 * @typedef StatusDTO Status data transfer object.
 * @property {number} [id] The id of the status.
 * @property {string} name The name of the status.
 */

/**
 * @typedef TaskDTO Task data transfer object.
 * @property {number} [id] The id of the task.
 * @property {string} title The title of the task.
 * @property {string} description The description of the task.
 * @property {Date} [startDate] The start date of the task.
 * @property {Date} endDate The end date of the task.
 * @property {string} status The status of the task.
 * @property {number} boardId The board id of the task.
 * @property {number} [assignerId] The assigner id of the task.
 */

export const BASE_URL = new URL(globalThis.config.uri).origin;

export const TOKEN_KEY = "auth-token";

export const API = {
    auth: `${BASE_URL}/api/auth`,
    project: `${BASE_URL}/api/project`
}

/**
 * verify the auth token and retrieve new auth token.
 *
 * @returns {Promise<UserDTO|null>} The details of the user.
 */
export async function verify() {
    if (!localStorage.getItem(TOKEN_KEY)) return null;

    try {
        const response = await fetch(API.auth, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem(TOKEN_KEY)}`
            }
        });

        if (response.ok) {
            const { token, ...details } = await response.json();

            localStorage.setItem(TOKEN_KEY, token);

            return details;
        } else {
            console.error(`Verification failed status: ${response.status}.`);
        }
    } catch (error) {
        console.log("Failed to verify auth token.", error);
    }

    localStorage.removeItem(TOKEN_KEY);
    return null;
}

/**
 * Login user account and retrieve auth token.
 *
 * @param {UserDTO} user The login payload.
 * @returns {Promise<UserDTO|null>} The details of the user.
 */
export async function login(user) {
    try {
        const response = await fetch(`${API.auth}/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(user)
        });

        if (!response.ok) {
            console.error(`Login failed status: ${response.status}.`);
        } else {
            const { token, ...details } = await response.json();

            localStorage.setItem(TOKEN_KEY, token);

            return details;
        }
    } catch (error) {
        console.log("Failed to login user account.", error);
    }

    return null;
}

/**
 * Register user account and retrieve auth token.
 *
 * @param {UserDTO} user The register payload.
 * @returns {Promise<UserDTO|null>} The details of the user.
 */
export async function register(user) {
    try {
        const response = await fetch(`${API.auth}/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(user)
        });

        if (response.ok) {
            const { token, ...details } = await response.json();

            localStorage.setItem(TOKEN_KEY, token);

            return details;
        } else {
            console.error(`Registration failed status: ${response.status}`);
        }
    } catch (error) {
        console.log("Failed to register user account.", error);
    }

    return null;
}

/**
 * Get all projects.
 *
 * @returns {Promise<[ProjectDTO]>}
 */
export async function getAllProjects() {
    try {
        const response = await fetch(API.project, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem(TOKEN_KEY)}`
            }
        });

        if (response.ok) {
            return await response.json();
        } else {
            console.error(`Fetching projects failed status: ${response.status}`);
        }
    } catch (error) {
        console.log("Failed to get all projects.", error);
    }

    return [];
}

/**
 * Get the target project based on id.
 *
 * @param {number} id The id of the project.
 * @returns {Promise<ProjectDTO|null>} The details of the project.
 */
export async function getProject(id) {
    try {
        const response = await fetch(`${API.project}/${id}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem(TOKEN_KEY)}`
            }
        });

        if (response.ok) {
            return await response.json();
        } else {
            console.error(`Fetching project failed status: ${response.status}`);
        }
    } catch (error) {
        console.log("Failed to get project.", error);
    }

    return null;
}

/**
 * Create one project.
 *
 * @param {ProjectDTO} project The creation payload.
 * @returns {Promise<ProjectDTO|null>} The details of the project.
 */
export async function saveProject(project) {
    try {
        const response = await fetch(API.project, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem(TOKEN_KEY)}`
            },
            body: JSON.stringify(project)
        });

        if (response.ok) {
            return await response.json();
        } else {
            console.error(`Saving project failed status: ${response.status}`);
        }
    } catch (error) {
        console.log("Failed to save project.", error);
    }

    return null;
}

/**
 * Get the target tasks based on id.
 *
 * @param {number} id The id of the task.
 * @returns {Promise<TaskDTO|null>} The details of the task.
 */
export async function getTask(id) {
    try {
        const response = await fetch(`${API.project}/tasks/${id}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem(TOKEN_KEY)}`
            }
        });

        if (response.ok) {
            return await response.json();
        } else {
            console.error(`Fetching task failed status: ${response.status}`);
        }
    } catch (error) {
        console.error("Failed to get task.", error);
    }

    return null;
}

/**
 * Create or update one task.
 *
 * @param {TaskDTO} task The task to save.
 * @returns {Promise<TaskDTO|null>} The details of the task.
 */
export async function saveTask(task) {
    try {
        const response = await fetch(`${API.project}/tasks/${task.id ?? ''}`, {
            method: task.id ? "PUT" : "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem(TOKEN_KEY)}`
            },
            body: JSON.stringify(task),
        });

        if (response.ok) {
            return await response.json();
        } else {
            console.error(`Saving task failed status: ${response.status}`);
        }
    } catch (error) {
        console.error("Failed to save task.", error);
    }

    return null;
}
