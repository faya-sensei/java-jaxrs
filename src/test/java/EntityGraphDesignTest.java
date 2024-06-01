import jakarta.persistence.*;
import org.faya.sensei.entities.BoardEntity;
import org.faya.sensei.entities.StatusEntity;
import org.faya.sensei.entities.TaskEntity;
import org.faya.sensei.entities.UserEntity;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntityGraphDesignTest {

    public static String parseAnnotationMissingMessage(String annotation, String className, String fieldName) {
        return String.join(" ", List.of(
                String.format("@%s annotation is missing", annotation),
                String.format("on the %s field", fieldName),
                String.format("in %s.", className)
        ));
    }

    @Nested
    public class UserEntityTests {

        private final String className = UserEntity.class.getSimpleName();

        @Test
        public void testEntityAnnotation() {
            assertTrue(
                    UserEntity.class.isAnnotationPresent(Entity.class),
                    parseAnnotationMissingMessage(Entity.class.getSimpleName(), className, null)
            );
        }

        @Test
        public void testIdAnnotation() throws Exception {
            Field idField = UserEntity.class.getDeclaredField("id");

            assertTrue(
                    idField.isAnnotationPresent(Id.class),
                    parseAnnotationMissingMessage(Id.class.getSimpleName(), className, "id")
            );
            assertTrue(
                    idField.isAnnotationPresent(GeneratedValue.class),
                    parseAnnotationMissingMessage(GeneratedValue.class.getSimpleName(), className, "id")
            );
        }

        @Test
        public void testBoardEntityRelationship() throws Exception {
            Field boardsField = UserEntity.class.getDeclaredField("boards");

            assertTrue(
                    boardsField.isAnnotationPresent(ManyToMany.class),
                    parseAnnotationMissingMessage(ManyToMany.class.getSimpleName(), className, "boards")
            );
        }

        @Test
        public void testTaskEntityRelationship() throws Exception {
            Field assignedTasksField = UserEntity.class.getDeclaredField("assignedTasks");

            assertTrue(
                    assignedTasksField.isAnnotationPresent(OneToMany.class),
                    parseAnnotationMissingMessage(OneToMany.class.getSimpleName(), className, "assignedTasks")
            );
        }

        @Test
        public void testGettersAndSetters() throws Exception {
            UserEntity user = new UserEntity();
            Integer id = 1;
            String name = "User";
            List<BoardEntity> boards = new ArrayList<>();
            List<TaskEntity> assignedTasks = new ArrayList<>();

            Method setId = UserEntity.class.getMethod("setId", Integer.class);
            Method getId = UserEntity.class.getMethod("getId");
            setId.invoke(user, id);
            assertEquals(id, getId.invoke(user), "getId or setId is incorrect in %s.".formatted(className));

            Method setName = UserEntity.class.getMethod("setName", String.class);
            Method getName = UserEntity.class.getMethod("getName");
            setName.invoke(user, name);
            assertEquals(name, getName.invoke(user), "getName or setName is incorrect in %s.".formatted(className));

            Method setBoards = UserEntity.class.getMethod("setBoards", List.class);
            Method getBoards = UserEntity.class.getMethod("getBoards");
            setBoards.invoke(user, boards);
            assertEquals(boards, getBoards.invoke(user), "getBoards or setBoards is incorrect in %s.".formatted(className));

            Method setAssignedTasks = UserEntity.class.getMethod("setAssignedTasks", List.class);
            Method getAssignedTasks = UserEntity.class.getMethod("getAssignedTasks");
            setAssignedTasks.invoke(user, assignedTasks);
            assertEquals(assignedTasks, getAssignedTasks.invoke(user), "getAssignedTasks or setAssignedTasks is incorrect in %s.".formatted(className));
        }
    }

    @Nested
    public class BoardEntityTests {

        private final String className = BoardEntity.class.getSimpleName();

        @Test
        public void testEntityAnnotation() {
            assertTrue(
                    BoardEntity.class.isAnnotationPresent(Entity.class),
                    parseAnnotationMissingMessage(Entity.class.getSimpleName(), className, null)
            );
        }

        @Test
        public void testIdAnnotation() throws Exception {
            Field idField = BoardEntity.class.getDeclaredField("id");

            assertTrue(
                    idField.isAnnotationPresent(Id.class),
                    parseAnnotationMissingMessage(Id.class.getSimpleName(), className, "id")
            );
            assertTrue(
                    idField.isAnnotationPresent(GeneratedValue.class),
                    parseAnnotationMissingMessage(GeneratedValue.class.getSimpleName(), className, "id")
            );
        }

        @Test
        public void testUserEntityRelationship() throws Exception {
            Field usersField = BoardEntity.class.getDeclaredField("users");

            assertTrue(
                    usersField.isAnnotationPresent(ManyToMany.class),
                    parseAnnotationMissingMessage(ManyToMany.class.getSimpleName(), className, "users")
            );
        }

        @Test
        public void testTaskEntityRelationship() throws Exception {
            Field tasksField = BoardEntity.class.getDeclaredField("tasks");

            assertTrue(
                    tasksField.isAnnotationPresent(OneToMany.class),
                    parseAnnotationMissingMessage(OneToMany.class.getSimpleName(), className, "tasks")
            );
        }

        @Test
        public void testStatusEntityRelationship() throws Exception {
            Field statusesField = BoardEntity.class.getDeclaredField("statuses");

            assertTrue(
                    statusesField.isAnnotationPresent(OneToMany.class),
                    parseAnnotationMissingMessage(OneToMany.class.getSimpleName(), className, "statuses")
            );
        }

        @Test
        public void testGettersAndSetters() throws Exception {
            BoardEntity board = new BoardEntity();
            Integer id = 1;
            List<UserEntity> users = new ArrayList<>();
            List<StatusEntity> statuses = new ArrayList<>();
            List<TaskEntity> tasks = new ArrayList<>();

            Method setId = BoardEntity.class.getMethod("setId", Integer.class);
            Method getId = BoardEntity.class.getMethod("getId");
            setId.invoke(board, id);
            assertEquals(id, getId.invoke(board), "getId or setId is incorrect in %s.".formatted(className));

            Method setUsers = BoardEntity.class.getMethod("setUsers", List.class);
            Method getUsers = BoardEntity.class.getMethod("getUsers");
            setUsers.invoke(board, users);
            assertEquals(users, getUsers.invoke(board), "getUsers or setUsers is incorrect in %s.".formatted(className));

            Method setStatuses = BoardEntity.class.getMethod("setStatuses", List.class);
            Method getStatuses = BoardEntity.class.getMethod("getStatuses");
            setStatuses.invoke(board, statuses);
            assertEquals(statuses, getStatuses.invoke(board), "getStatuses or setStatuses is incorrect in %s.".formatted(className));

            Method setTasks = BoardEntity.class.getMethod("setTasks", List.class);
            Method getTasks = BoardEntity.class.getMethod("getTasks");
            setTasks.invoke(board, tasks);
            assertEquals(tasks, getTasks.invoke(board), "getTasks or setTasks is incorrect in %s.".formatted(className));
        }
    }

    @Nested
    public class StatusEntityTests {

        private final String className = StatusEntity.class.getSimpleName();

        @Test
        public void testEntityAnnotation() {
            assertTrue(
                    StatusEntity.class.isAnnotationPresent(Entity.class),
                    parseAnnotationMissingMessage(Entity.class.getSimpleName(), className, null)
            );
        }

        @Test
        public void testIdAnnotation() throws Exception {
            Field idField = StatusEntity.class.getDeclaredField("id");

            assertTrue(
                    idField.isAnnotationPresent(Id.class),
                    parseAnnotationMissingMessage(Id.class.getSimpleName(), className, "id")
            );
            assertTrue(
                    idField.isAnnotationPresent(GeneratedValue.class),
                    parseAnnotationMissingMessage(GeneratedValue.class.getSimpleName(), className, "id")
            );
        }

        @Test
        public void testBoardEntityRelationship() throws Exception {
            Field boardField = StatusEntity.class.getDeclaredField("board");

            assertTrue(
                    boardField.isAnnotationPresent(ManyToOne.class),
                    parseAnnotationMissingMessage(ManyToOne.class.getSimpleName(), className, "board")
            );
        }

        @Test
        public void testTaskEntityRelationship() throws Exception {
            Field tasksField = StatusEntity.class.getDeclaredField("tasks");

            assertTrue(
                    tasksField.isAnnotationPresent(OneToMany.class),
                    parseAnnotationMissingMessage(OneToMany.class.getSimpleName(), className, "tasks")
            );
        }

        @Test
        public void testGettersAndSetters() throws Exception {
            StatusEntity status = new StatusEntity();
            Integer id = 1;
            String name = "Todo";
            BoardEntity board = new BoardEntity();
            List<TaskEntity> tasks = new ArrayList<>();

            Method setId = StatusEntity.class.getMethod("setId", Integer.class);
            Method getId = StatusEntity.class.getMethod("getId");
            setId.invoke(status, id);
            assertEquals(id, getId.invoke(status), "getId or setId is incorrect in %s.".formatted(className));

            Method setName = StatusEntity.class.getMethod("setName", String.class);
            Method getName = StatusEntity.class.getMethod("getName");
            setName.invoke(status, name);
            assertEquals(name, getName.invoke(status), "getName or setName is incorrect in %s.".formatted(className));

            Method setBoard = StatusEntity.class.getMethod("setBoard", BoardEntity.class);
            Method getBoard = StatusEntity.class.getMethod("getBoard");
            setBoard.invoke(status, board);
            assertEquals(board, getBoard.invoke(status), "getBoard or setBoard is incorrect in %s.".formatted(className));

            Method setTasks = StatusEntity.class.getMethod("setTasks", List.class);
            Method getTasks = StatusEntity.class.getMethod("getTasks");
            setTasks.invoke(status, tasks);
            assertEquals(tasks, getTasks.invoke(status), "getTasks or setTasks is incorrect in %s.".formatted(className));
        }
    }

    @Nested
    public class TaskEntityTests {

        private final String className = TaskEntity.class.getSimpleName();

        @Test
        public void testEntityAnnotation() {
            assertTrue(
                    TaskEntity.class.isAnnotationPresent(Entity.class),
                    parseAnnotationMissingMessage(Entity.class.getSimpleName(), className, null)
            );
        }

        @Test
        public void testIdAnnotation() throws Exception {
            Field idField = TaskEntity.class.getDeclaredField("id");

            assertTrue(
                    idField.isAnnotationPresent(Id.class),
                    parseAnnotationMissingMessage(Id.class.getSimpleName(), className, "id")
            );
            assertTrue(
                    idField.isAnnotationPresent(GeneratedValue.class),
                    parseAnnotationMissingMessage(GeneratedValue.class.getSimpleName(), className, "id")
            );
        }

        @Test
        public void testManyToOneAnnotationOnBoard() throws Exception {
            Field boardField = TaskEntity.class.getDeclaredField("board");

            assertTrue(
                    boardField.isAnnotationPresent(ManyToOne.class),
                    parseAnnotationMissingMessage(ManyToOne.class.getSimpleName(), className, "board")
            );
        }

        @Test
        public void testManyToOneAnnotationOnStatus() throws Exception {
            Field statusField = TaskEntity.class.getDeclaredField("status");

            assertTrue(
                    statusField.isAnnotationPresent(ManyToOne.class),
                    parseAnnotationMissingMessage(ManyToOne.class.getSimpleName(), className, "status")
            );
        }

        @Test
        public void testManyToOneAnnotationOnAssigner() throws Exception {
            Field assignerField = TaskEntity.class.getDeclaredField("assigner");

            assertTrue(
                    assignerField.isAnnotationPresent(ManyToOne.class),
                    parseAnnotationMissingMessage(ManyToOne.class.getSimpleName(), className, "assigner")
            );
        }

        @Test
        public void testGettersAndSetters() throws Exception {
            TaskEntity task = new TaskEntity();
            Integer id = 1;
            String title = "Task Title";
            String description = "Task Description";
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = LocalDateTime.now().plusDays(1);
            BoardEntity board = new BoardEntity();
            StatusEntity status = new StatusEntity();
            UserEntity assigner = new UserEntity();

            Method setId = TaskEntity.class.getMethod("setId", Integer.class);
            Method getId = TaskEntity.class.getMethod("getId");
            setId.invoke(task, id);
            assertEquals(id, getId.invoke(task), "getId or setId is incorrect in %s.".formatted(className));

            Method setTitle = TaskEntity.class.getMethod("setTitle", String.class);
            Method getTitle = TaskEntity.class.getMethod("getTitle");
            setTitle.invoke(task, title);
            assertEquals(title, getTitle.invoke(task), "getTitle or setTitle is incorrect in %s.".formatted(className));

            Method setDescription = TaskEntity.class.getMethod("setDescription", String.class);
            Method getDescription = TaskEntity.class.getMethod("getDescription");
            setDescription.invoke(task, description);
            assertEquals(description, getDescription.invoke(task), "getDescription or setDescription is incorrect in %s.".formatted(className));

            Method setStartDate = TaskEntity.class.getMethod("setStartDate", LocalDateTime.class);
            Method getStartDate = TaskEntity.class.getMethod("getStartDate");
            setStartDate.invoke(task, startDate);
            assertEquals(startDate, getStartDate.invoke(task), "getStartDate or setStartDate is incorrect in %s.".formatted(className));

            Method setEndDate = TaskEntity.class.getMethod("setEndDate", LocalDateTime.class);
            Method getEndDate = TaskEntity.class.getMethod("getEndDate");
            setEndDate.invoke(task, endDate);
            assertEquals(endDate, getEndDate.invoke(task), "getEndDate or setEndDate is incorrect in %s.".formatted(className));

            Method setBoard = TaskEntity.class.getMethod("setBoard", BoardEntity.class);
            Method getBoard = TaskEntity.class.getMethod("getBoard");
            setBoard.invoke(task, board);
            assertEquals(board, getBoard.invoke(task), "getBoard or setBoard is incorrect in %s.".formatted(className));

            Method setStatus = TaskEntity.class.getMethod("setStatus", StatusEntity.class);
            Method getStatus = TaskEntity.class.getMethod("getStatus");
            setStatus.invoke(task, status);
            assertEquals(status, getStatus.invoke(task), "getStatus or setStatus is incorrect in %s.".formatted(className));

            Method setAssigner = TaskEntity.class.getMethod("setAssigner", UserEntity.class);
            Method getAssigner = TaskEntity.class.getMethod("getAssigner");
            setAssigner.invoke(task, assigner);
            assertEquals(assigner, getAssigner.invoke(task), "getAssigner or setAssigner is incorrect in %s.".formatted(className));
        }
    }
}
