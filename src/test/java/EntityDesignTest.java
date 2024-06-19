import jakarta.persistence.*;
import org.faya.sensei.entities.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntityDesignTest {

    public static String parseAnnotationMissingMessage(final String annotation, final String className, final String fieldName) {
        return String.format("@%s annotation is missing", annotation) +
                (fieldName != null ? String.format("on the %s field", fieldName) : "") +
                String.format("in %s.", className);
    }

    public static String parseGetterSetterErrorMessage(final String className, final String fieldName) {
        final String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        final String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        return String.format("%s or %s is missing or incorrect implemented in %s.", getterName, setterName, className);
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
            final Field idField = UserEntity.class.getDeclaredField("id");

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
        public void testProjectEntityRelationship() throws Exception {
            final Field projectsField = UserEntity.class.getDeclaredField("projects");

            assertTrue(
                    projectsField.isAnnotationPresent(ManyToMany.class),
                    parseAnnotationMissingMessage(ManyToMany.class.getSimpleName(), className, "projects")
            );
        }

        @Test
        public void testTaskEntityRelationship() throws Exception {
            final Field assignedTasksField = UserEntity.class.getDeclaredField("assignedTasks");

            assertTrue(
                    assignedTasksField.isAnnotationPresent(OneToMany.class),
                    parseAnnotationMissingMessage(OneToMany.class.getSimpleName(), className, "assignedTasks")
            );
        }

        @Test
        public void testGettersAndSetters() throws Exception {
            UserEntity user = new UserEntity();
            final Integer id = 1;
            final String name = "User";
            final UserRole role = UserRole.ADMIN;
            final List<ProjectEntity> projects = new ArrayList<>();
            final List<TaskEntity> assignedTasks = new ArrayList<>();

            final Method setId = UserEntity.class.getMethod("setId", Integer.class);
            final Method getId = UserEntity.class.getMethod("getId");
            setId.invoke(user, id);
            assertEquals(id, getId.invoke(user), parseGetterSetterErrorMessage(className, "id"));

            final Method setName = UserEntity.class.getMethod("setName", String.class);
            final Method getName = UserEntity.class.getMethod("getName");
            setName.invoke(user, name);
            assertEquals(name, getName.invoke(user), parseGetterSetterErrorMessage(className, "name"));

            final Method setRole = UserEntity.class.getMethod("setRole", UserRole.class);
            final Method getRole = UserEntity.class.getMethod("getRole");
            setRole.invoke(user, role);
            assertEquals(role, getRole.invoke(user), parseGetterSetterErrorMessage(className, "role"));

            final Method setProjects = UserEntity.class.getMethod("setProjects", List.class);
            final Method getProjects = UserEntity.class.getMethod("getProjects");
            setProjects.invoke(user, projects);
            assertEquals(projects, getProjects.invoke(user), parseGetterSetterErrorMessage(className, "projects"));

            final Method setAssignedTasks = UserEntity.class.getMethod("setAssignedTasks", List.class);
            final Method getAssignedTasks = UserEntity.class.getMethod("getAssignedTasks");
            setAssignedTasks.invoke(user, assignedTasks);
            assertEquals(assignedTasks, getAssignedTasks.invoke(user), parseGetterSetterErrorMessage(className, "assignedTasks"));
        }
    }

    @Nested
    public class ProjectEntityTests {

        private final String className = ProjectEntity.class.getSimpleName();

        @Test
        public void testEntityAnnotation() {
            assertTrue(
                    ProjectEntity.class.isAnnotationPresent(Entity.class),
                    parseAnnotationMissingMessage(Entity.class.getSimpleName(), className, null)
            );
        }

        @Test
        public void testIdAnnotation() throws Exception {
            final Field idField = ProjectEntity.class.getDeclaredField("id");

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
            final Field usersField = ProjectEntity.class.getDeclaredField("users");

            assertTrue(
                    usersField.isAnnotationPresent(ManyToMany.class),
                    parseAnnotationMissingMessage(ManyToMany.class.getSimpleName(), className, "users")
            );
        }

        @Test
        public void testTaskEntityRelationship() throws Exception {
            final Field tasksField = ProjectEntity.class.getDeclaredField("tasks");

            assertTrue(
                    tasksField.isAnnotationPresent(OneToMany.class),
                    parseAnnotationMissingMessage(OneToMany.class.getSimpleName(), className, "tasks")
            );
        }

        @Test
        public void testStatusEntityRelationship() throws Exception {
            final Field statusesField = ProjectEntity.class.getDeclaredField("statuses");

            assertTrue(
                    statusesField.isAnnotationPresent(OneToMany.class),
                    parseAnnotationMissingMessage(OneToMany.class.getSimpleName(), className, "statuses")
            );
        }

        @Test
        public void testGettersAndSetters() throws Exception {
            ProjectEntity project = new ProjectEntity();
            final Integer id = 1;
            final List<UserEntity> users = new ArrayList<>();
            final List<StatusEntity> statuses = new ArrayList<>();
            final List<TaskEntity> tasks = new ArrayList<>();

            final Method setId = ProjectEntity.class.getMethod("setId", Integer.class);
            final Method getId = ProjectEntity.class.getMethod("getId");
            setId.invoke(project, id);
            assertEquals(id, getId.invoke(project), parseGetterSetterErrorMessage(className, "id"));

            final Method setUsers = ProjectEntity.class.getMethod("setUsers", List.class);
            final Method getUsers = ProjectEntity.class.getMethod("getUsers");
            setUsers.invoke(project, users);
            assertEquals(users, getUsers.invoke(project), parseGetterSetterErrorMessage(className, "users"));

            final Method setStatuses = ProjectEntity.class.getMethod("setStatuses", List.class);
            final Method getStatuses = ProjectEntity.class.getMethod("getStatuses");
            setStatuses.invoke(project, statuses);
            assertEquals(statuses, getStatuses.invoke(project), parseGetterSetterErrorMessage(className, "statuses"));

            final Method setTasks = ProjectEntity.class.getMethod("setTasks", List.class);
            final Method getTasks = ProjectEntity.class.getMethod("getTasks");
            setTasks.invoke(project, tasks);
            assertEquals(tasks, getTasks.invoke(project), parseGetterSetterErrorMessage(className, "tasks"));
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
            final Field idField = StatusEntity.class.getDeclaredField("id");

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
        public void testProjectEntityRelationship() throws Exception {
            final Field projectField = StatusEntity.class.getDeclaredField("project");

            assertTrue(
                    projectField.isAnnotationPresent(ManyToOne.class),
                    parseAnnotationMissingMessage(ManyToOne.class.getSimpleName(), className, "project")
            );
        }

        @Test
        public void testTaskEntityRelationship() throws Exception {
            final Field tasksField = StatusEntity.class.getDeclaredField("tasks");

            assertTrue(
                    tasksField.isAnnotationPresent(OneToMany.class),
                    parseAnnotationMissingMessage(OneToMany.class.getSimpleName(), className, "tasks")
            );
        }

        @Test
        public void testGettersAndSetters() throws Exception {
            StatusEntity status = new StatusEntity();
            final Integer id = 1;
            final String name = "Todo";
            final ProjectEntity project = new ProjectEntity();
            final List<TaskEntity> tasks = new ArrayList<>();

            final Method setId = StatusEntity.class.getMethod("setId", Integer.class);
            final Method getId = StatusEntity.class.getMethod("getId");
            setId.invoke(status, id);
            assertEquals(id, getId.invoke(status), parseGetterSetterErrorMessage(className, "id"));

            final Method setName = StatusEntity.class.getMethod("setName", String.class);
            final Method getName = StatusEntity.class.getMethod("getName");
            setName.invoke(status, name);
            assertEquals(name, getName.invoke(status), parseGetterSetterErrorMessage(className, "name"));

            final Method setProject = StatusEntity.class.getMethod("setProject", ProjectEntity.class);
            final Method getProject = StatusEntity.class.getMethod("getProject");
            setProject.invoke(status, project);
            assertEquals(project, getProject.invoke(status), parseGetterSetterErrorMessage(className, "project"));

            final Method setTasks = StatusEntity.class.getMethod("setTasks", List.class);
            final Method getTasks = StatusEntity.class.getMethod("getTasks");
            setTasks.invoke(status, tasks);
            assertEquals(tasks, getTasks.invoke(status), parseGetterSetterErrorMessage(className, "tasks"));
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
            final Field idField = TaskEntity.class.getDeclaredField("id");

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
        public void testManyToOneAnnotationOnProject() throws Exception {
            final Field projectField = TaskEntity.class.getDeclaredField("project");

            assertTrue(
                    projectField.isAnnotationPresent(ManyToOne.class),
                    parseAnnotationMissingMessage(ManyToOne.class.getSimpleName(), className, "project")
            );
        }

        @Test
        public void testManyToOneAnnotationOnStatus() throws Exception {
            final Field statusField = TaskEntity.class.getDeclaredField("status");

            assertTrue(
                    statusField.isAnnotationPresent(ManyToOne.class),
                    parseAnnotationMissingMessage(ManyToOne.class.getSimpleName(), className, "status")
            );
        }

        @Test
        public void testManyToOneAnnotationOnAssigner() throws Exception {
            final Field assignerField = TaskEntity.class.getDeclaredField("assigner");

            assertTrue(
                    assignerField.isAnnotationPresent(ManyToOne.class),
                    parseAnnotationMissingMessage(ManyToOne.class.getSimpleName(), className, "assigner")
            );
        }

        @Test
        public void testGettersAndSetters() throws Exception {
            TaskEntity task = new TaskEntity();
            final Integer id = 1;
            final String title = "Task Title";
            final String description = "Task Description";
            final LocalDateTime startDate = LocalDateTime.now();
            final LocalDateTime endDate = LocalDateTime.now().plusDays(1);
            final ProjectEntity project = new ProjectEntity();
            final StatusEntity status = new StatusEntity();
            final UserEntity assigner = new UserEntity();

            final Method setId = TaskEntity.class.getMethod("setId", Integer.class);
            final Method getId = TaskEntity.class.getMethod("getId");
            setId.invoke(task, id);
            assertEquals(id, getId.invoke(task), parseGetterSetterErrorMessage(className, "id"));

            final Method setTitle = TaskEntity.class.getMethod("setTitle", String.class);
            final Method getTitle = TaskEntity.class.getMethod("getTitle");
            setTitle.invoke(task, title);
            assertEquals(title, getTitle.invoke(task), parseGetterSetterErrorMessage(className, "title"));

            final Method setDescription = TaskEntity.class.getMethod("setDescription", String.class);
            final Method getDescription = TaskEntity.class.getMethod("getDescription");
            setDescription.invoke(task, description);
            assertEquals(description, getDescription.invoke(task), parseGetterSetterErrorMessage(className, "description"));

            final Method setStartDate = TaskEntity.class.getMethod("setStartDate", LocalDateTime.class);
            final Method getStartDate = TaskEntity.class.getMethod("getStartDate");
            setStartDate.invoke(task, startDate);
            assertEquals(startDate, getStartDate.invoke(task), parseGetterSetterErrorMessage(className, "startDate"));

            final Method setEndDate = TaskEntity.class.getMethod("setEndDate", LocalDateTime.class);
            final Method getEndDate = TaskEntity.class.getMethod("getEndDate");
            setEndDate.invoke(task, endDate);
            assertEquals(endDate, getEndDate.invoke(task), parseGetterSetterErrorMessage(className, "endDate"));

            final Method setProject = TaskEntity.class.getMethod("setProject", ProjectEntity.class);
            final Method getProject = TaskEntity.class.getMethod("getProject");
            setProject.invoke(task, project);
            assertEquals(project, getProject.invoke(task), parseGetterSetterErrorMessage(className, "project"));

            final Method setStatus = TaskEntity.class.getMethod("setStatus", StatusEntity.class);
            final Method getStatus = TaskEntity.class.getMethod("getStatus");
            setStatus.invoke(task, status);
            assertEquals(status, getStatus.invoke(task), parseGetterSetterErrorMessage(className, "status"));

            final Method setAssigner = TaskEntity.class.getMethod("setAssigner", UserEntity.class);
            final Method getAssigner = TaskEntity.class.getMethod("getAssigner");
            setAssigner.invoke(task, assigner);
            assertEquals(assigner, getAssigner.invoke(task), parseGetterSetterErrorMessage(className, "assigner"));
        }
    }
}
