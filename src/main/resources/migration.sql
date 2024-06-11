INSERT INTO users (name, password, role) VALUES ('Test', '', 'ADMIN');

INSERT INTO projects (name) VALUES ('Project 1');

INSERT INTO user_project VALUES (1, 1);

INSERT INTO statuses (name, project_id)
VALUES
    ('Todo', 1),
    ('Done', 1);

INSERT INTO tasks (title, description, startDate, endDate, project_id, status_id, assigner_id)
VALUES
    ('Task 1', 'Task 1 Description.', NOW() - INTERVAL '20' MINUTE, NOW() + INTERVAL '10' MINUTE, 1, 1, 1),
    ('Task 2', 'Task 2 Description.', NOW() - INTERVAL '10' MINUTE, NOW() + INTERVAL '20' MINUTE, 1, 2, 1);