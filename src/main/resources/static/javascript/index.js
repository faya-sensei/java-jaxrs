import { TaskBoard } from "./components/task-board.js";
import { TaskComponent } from "./components/task-component.js";
import { TaskStatus } from "./components/task-status.js";

const app = document.getElementById("app");
app.append(new TaskBoard());