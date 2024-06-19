import { TaskForm } from "./components/task-form.js";
import { TaskComponent } from "./components/task-component.js";
import { TaskStatus } from "./components/task-status.js";
import { TaskBoard } from "./components/task-board.js";

const app = document.getElementById("app");
app.append(new TaskBoard());
