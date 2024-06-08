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

/**
 * @typedef UserDTO User data transfer object.
 * @property {number} [id] The id of the user.
 * @property {string} name The name of the user.
 */

export const BASE_URL = new URL(globalThis.config.uri).origin

export const API = {
    todos: `${BASE_URL}/api/todos`,
}

/**
 * Get all tasks.
 *
 * @returns {Promise<TaskDTO[]>}
 */
export async function getAllTasks() {
    try {
        const response = await fetch(API.todos);

        return await response.json();
    } catch (error) {
        console.log(error);
    }
}

/**
 * Get the target tasks based on id.
 *
 * @param {number} id The id task.
 * @returns {Promise<TaskDTO>}
 */
export async function getTask(id) {
    try {
        const response = await fetch(`${API.todos}/${id}`);

        return await response.json();
    } catch (error) {
        console.log(error);
    }
}

/**
 * Save one task.
 *
 * @param {TaskDTO} task The task to save.
 * @returns {Promise<TaskDTO>} The full info of the task.
 */
export async function saveTask(task) {
    try {
        const response = await fetch(API.todos, {
            method: "POST",
            body: JSON.stringify(task),
        });

        return await response.json();
    } catch (error) {
        console.log(error);
    }
}